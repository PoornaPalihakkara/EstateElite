package com.estateelite.service;

import com.estateelite.dto.request.DemoPaymentRequest;
import com.estateelite.dto.request.ReservePropertyRequest;
import com.estateelite.dto.request.TransactionRequest;
import com.estateelite.dto.request.TransactionStatusUpdateRequest;
import com.estateelite.dto.response.TransactionResponse;
import com.estateelite.exception.AccessDeniedCustomException;
import com.estateelite.exception.BadRequestException;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.*;
import com.estateelite.pattern.builder.InvoiceBuilderDirector;
import com.estateelite.pattern.strategy.BankTransferPaymentStrategy;
import com.estateelite.pattern.strategy.CardPaymentStrategy;
import com.estateelite.pattern.strategy.PaymentContext;
import com.estateelite.pattern.strategy.PaymentStrategy;
import com.estateelite.repository.PropertyRepository;
import com.estateelite.repository.TransactionRepository;
import com.estateelite.repository.UserRepository;
import com.estateelite.util.PdfInvoiceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PdfInvoiceGenerator pdfInvoiceGenerator;
    private final NotificationService notificationService;
    private final CardPaymentStrategy cardPaymentStrategy;
    private final BankTransferPaymentStrategy bankTransferPaymentStrategy;


    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        Transaction transaction = InvoiceBuilderDirector.buildDraftInvoice(property, buyer, request.getAmount());
        transactionRepository.save(transaction);
        return TransactionResponse.from(transaction);
    }


    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll() {
        return transactionRepository.findAll().stream().map(TransactionResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getByBuyer(Long buyerId) {
        return transactionRepository.findByBuyerId(buyerId).stream().map(TransactionResponse::from).collect(Collectors.toList());
    }


    @Transactional
    public TransactionResponse updateStatus(Long id, TransactionStatusUpdateRequest request, User financeOfficer) {
        Transaction transaction = findOrThrow(id);
        transaction.setStatus(request.getStatus());
        transaction.setFinanceOfficer(financeOfficer);
        transactionRepository.save(transaction);

        // Keep property status in sync with the payment outcome
        applyPropertyStatusFromTransaction(transaction);

        notificationService.notify(transaction.getBuyer(),
                "Your transaction " + transaction.getInvoiceNumber() + " is now " + request.getStatus(),
                NotificationType.TRANSACTION_UPDATE);

        return TransactionResponse.from(transaction);
    }


    @Transactional
    public TransactionResponse reserveWithPayment(ReservePropertyRequest request, User buyer) {
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new BadRequestException(
                    "Property is not available for reservation (current status: " + property.getStatus() + ")");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null) {
            amount = property.getPrice();
        } else if (amount.compareTo(property.getPrice()) > 0) {
            throw new BadRequestException("Reservation amount cannot exceed the listing price ("
                    + property.getPrice() + " LKR)");
        }

        // Create draft invoice for this buyer
        Transaction transaction = InvoiceBuilderDirector.buildDraftInvoice(property, buyer, amount);
        transactionRepository.save(transaction);

        // Process card payment via existing Strategy
        DemoPaymentRequest card = new DemoPaymentRequest();
        card.setCardholderName(request.getCardholderName());
        card.setCardNumber(request.getCardNumber());
        card.setExpiry(request.getExpiry());
        card.setCvv(request.getCvv());

        PaymentStrategy strategy = cardPaymentStrategy;
        PaymentContext context = PaymentContext.builder()
                .cardholderName(card.getCardholderName())
                .cardNumber(card.getCardNumber())
                .expiry(card.getExpiry())
                .cvv(card.getCvv())
                .build();

        strategy.process(transaction, buyer, context);
        transactionRepository.save(transaction);

        // Full payment → SOLD; partial deposit → RESERVED
        if (amount.compareTo(property.getPrice()) >= 0) {
            property.setStatus(PropertyStatus.SOLD);
        } else {
            property.setStatus(PropertyStatus.RESERVED);
        }
        propertyRepository.save(property);

        String statusLabel = property.getStatus().name();
        notificationService.notify(buyer,
                "Reservation successful for \"" + property.getTitle() + "\". Invoice "
                        + transaction.getInvoiceNumber() + " is COMPLETED. Property is now "
                        + statusLabel + ".",
                NotificationType.TRANSACTION_UPDATE);

        if (property.getSeller() != null) {
            notificationService.notify(property.getSeller(),
                    "Your listing \"" + property.getTitle() + "\" is now " + statusLabel
                            + " (buyer: " + buyer.getFullName()
                            + ", invoice " + transaction.getInvoiceNumber() + ").",
                    NotificationType.LISTING_STATUS_CHANGED);
        }

        return TransactionResponse.from(transaction);
    }


    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request, User financeOfficer) {
        Transaction transaction = findOrThrow(id);
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new BadRequestException("Completed invoices cannot be edited - refund them instead");
        }
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found"));

        transaction.setProperty(property);
        transaction.setBuyer(buyer);
        transaction.setAmount(request.getAmount());
        transaction.setFinanceOfficer(financeOfficer);
        transactionRepository.save(transaction);
        return TransactionResponse.from(transaction);
    }


    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = findOrThrow(id);
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new BadRequestException("Completed transactions cannot be deleted - refund them instead");
        }
        transactionRepository.delete(transaction);
    }


    @Transactional(readOnly = true)
    public byte[] exportInvoicePdf(Long id) {
        Transaction transaction = findOrThrow(id);
        return pdfInvoiceGenerator.generate(transaction);
    }


    @Transactional
    public TransactionResponse reserveWithBankTransfer(Long propertyId,
                                                       java.math.BigDecimal amount,
                                                       MultipartFile receipt,
                                                       String transferRef,
                                                       User buyer) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (property.getStatus() != PropertyStatus.AVAILABLE) {
            throw new BadRequestException(
                    "Property is not available for reservation (current status: " + property.getStatus() + ")");
        }

        if (receipt == null || receipt.isEmpty()) {
            throw new BadRequestException("Bank transfer requires a photo of the transfer receipt");
        }

        java.math.BigDecimal payAmount = amount;
        if (payAmount == null) {
            payAmount = property.getPrice();
        } else if (payAmount.compareTo(property.getPrice()) > 0) {
            throw new BadRequestException("Reservation amount cannot exceed the listing price ("
                    + property.getPrice() + " LKR)");
        }

        Transaction transaction = InvoiceBuilderDirector.buildDraftInvoice(property, buyer, payAmount);
        transactionRepository.save(transaction);

        PaymentStrategy strategy = bankTransferPaymentStrategy;
        PaymentContext context = PaymentContext.builder()
                .receiptFile(receipt)
                .transferReference(transferRef)
                .build();
        strategy.process(transaction, buyer, context);
        transactionRepository.save(transaction);

        // Bank transfer awaits verification — still lock the listing as RESERVED
        property.setStatus(PropertyStatus.RESERVED);
        propertyRepository.save(property);

        notificationService.notify(buyer,
                "Reservation submitted for \"" + property.getTitle() + "\" via bank transfer. Invoice "
                        + transaction.getInvoiceNumber() + " is PENDING verification. Property is RESERVED.",
                NotificationType.TRANSACTION_UPDATE);

        if (property.getSeller() != null) {
            notificationService.notify(property.getSeller(),
                    "Your listing \"" + property.getTitle() + "\" is now RESERVED (bank transfer pending verification).",
                    NotificationType.LISTING_STATUS_CHANGED);
        }

        return TransactionResponse.from(transaction);
    }


    @Transactional
    public TransactionResponse processPayment(Long id,
                                              PaymentMethod method,
                                              DemoPaymentRequest cardRequest,
                                              MultipartFile receiptFile,
                                              String transferRef,
                                              User buyer) {
        Transaction transaction = findOrThrow(id);
        validateOwnershipAndStatus(transaction, buyer);

        PaymentStrategy strategy = resolveStrategy(method);
        PaymentContext context = buildContext(method, cardRequest, receiptFile, transferRef);

        strategy.process(transaction, buyer, context);
        transactionRepository.save(transaction);

        // On successful payment, reserve the linked property (if still available)
        applyPropertyStatusFromTransaction(transaction);

        String msg = buildNotificationMessage(transaction, strategy);
        notificationService.notify(buyer, msg, NotificationType.TRANSACTION_UPDATE);

        return TransactionResponse.from(transaction);
    }


    @Transactional
    public TransactionResponse payDemo(Long id, DemoPaymentRequest request, User buyer) {
        return processPayment(id, PaymentMethod.CARD, request, null, null, buyer);
    }


    private void applyPropertyStatusFromTransaction(Transaction transaction) {
        Property property = transaction.getProperty();
        if (property == null) return;

        TransactionStatus status = transaction.getStatus();
        if (status == TransactionStatus.COMPLETED) {
            // Full payment (amount ≥ listing price) → SOLD; deposit → RESERVED
            if (transaction.getAmount() != null
                    && property.getPrice() != null
                    && transaction.getAmount().compareTo(property.getPrice()) >= 0) {
                property.setStatus(PropertyStatus.SOLD);
            } else {
                property.setStatus(PropertyStatus.RESERVED);
            }
            propertyRepository.save(property);
        } else if (status == TransactionStatus.PENDING) {
            if (property.getStatus() == PropertyStatus.AVAILABLE) {
                property.setStatus(PropertyStatus.RESERVED);
                propertyRepository.save(property);
            }
        } else if (status == TransactionStatus.REFUNDED) {
            if (property.getStatus() == PropertyStatus.RESERVED || property.getStatus() == PropertyStatus.SOLD) {
                property.setStatus(PropertyStatus.AVAILABLE);
                propertyRepository.save(property);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void validateOwnershipAndStatus(Transaction transaction, User buyer) {
        if (!transaction.getBuyer().getId().equals(buyer.getId())) {
            throw new AccessDeniedCustomException("You can only pay your own invoices");
        }
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new BadRequestException("This invoice is already paid");
        }
        if (transaction.getStatus() == TransactionStatus.REFUNDED) {
            throw new BadRequestException("Refunded invoices cannot be paid");
        }
    }

    private PaymentStrategy resolveStrategy(PaymentMethod method) {
        if (method == null) {
            throw new BadRequestException("Payment method is required (CARD or BANK_TRANSFER)");
        }
        return switch (method) {
            case CARD -> cardPaymentStrategy;
            case BANK_TRANSFER -> bankTransferPaymentStrategy;
        };
    }

    private PaymentContext buildContext(PaymentMethod method,
                                        DemoPaymentRequest cardRequest,
                                        MultipartFile receiptFile,
                                        String transferRef) {
        if (method == PaymentMethod.CARD) {
            if (cardRequest == null) {
                throw new BadRequestException("Card details are required for CARD payment");
            }
            return PaymentContext.builder()
                    .cardholderName(cardRequest.getCardholderName())
                    .cardNumber(cardRequest.getCardNumber())
                    .expiry(cardRequest.getExpiry())
                    .cvv(cardRequest.getCvv())
                    .build();
        }
        // BANK_TRANSFER
        return PaymentContext.builder()
                .receiptFile(receiptFile)
                .transferReference(transferRef)
                .build();
    }

    private String buildNotificationMessage(Transaction transaction, PaymentStrategy strategy) {
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            return "Payment successful via " + strategy.getMethodName()
                    + " for invoice " + transaction.getInvoiceNumber()
                    + " (" + transaction.getAmount() + " LKR). Receipt is ready to download.";
        }
        // PENDING – bank transfer awaiting verification
        return "Bank transfer receipt uploaded for invoice " + transaction.getInvoiceNumber()
                + ". Status is PENDING – a finance officer will verify your payment shortly.";
    }

    private Transaction findOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }
}
