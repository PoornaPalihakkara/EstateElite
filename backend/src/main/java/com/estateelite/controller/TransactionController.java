package com.estateelite.controller;

import com.estateelite.dto.request.DemoPaymentRequest;
import com.estateelite.dto.request.ReservePropertyRequest;
import com.estateelite.dto.request.TransactionRequest;
import com.estateelite.dto.request.TransactionStatusUpdateRequest;
import com.estateelite.dto.response.TransactionResponse;
import com.estateelite.model.PaymentMethod;
import com.estateelite.model.User;
import com.estateelite.service.CurrentUserService;
import com.estateelite.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.ok(transactionService.createTransaction(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> getAll() {
        return ResponseEntity.ok(transactionService.getAll());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<List<TransactionResponse>> getMine() {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(transactionService.getByBuyer(buyer.getId()));
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<TransactionResponse> reserveProperty(
            @Valid @RequestBody ReservePropertyRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(transactionService.reserveWithPayment(request, buyer));
    }

    @PostMapping(value = "/reserve/bank-transfer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<TransactionResponse> reserveWithBankTransfer(
            @RequestParam("propertyId") Long propertyId,
            @RequestParam(value = "amount", required = false) java.math.BigDecimal amount,
            @RequestPart("receipt") MultipartFile receipt,
            @RequestParam(value = "reference", required = false) String reference) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(
                transactionService.reserveWithBankTransfer(propertyId, amount, receipt, reference, buyer));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> updateStatus(@PathVariable Long id,
                                                            @Valid @RequestBody TransactionStatusUpdateRequest request) {
        User financeOfficer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(transactionService.updateStatus(id, request, financeOfficer));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody TransactionRequest request) {
        User financeOfficer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(transactionService.updateTransaction(id, request, financeOfficer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FINANCE_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Long id) {
        byte[] pdf = transactionService.exportInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<TransactionResponse> payDemo(@PathVariable Long id,
                                                       @Valid @RequestBody DemoPaymentRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(transactionService.payDemo(id, request, buyer));
    }

    @PostMapping("/{id}/pay/card")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<TransactionResponse> payWithCard(@PathVariable Long id,
                                                           @Valid @RequestBody DemoPaymentRequest request) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(
                transactionService.processPayment(id, PaymentMethod.CARD, request, null, null, buyer));
    }

    @PostMapping(value = "/{id}/pay/bank-transfer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<TransactionResponse> payWithBankTransfer(
            @PathVariable Long id,
            @RequestPart("receipt") MultipartFile receipt,
            @RequestParam(value = "reference", required = false) String reference) {
        User buyer = currentUserService.getCurrentUser();
        return ResponseEntity.ok(
                transactionService.processPayment(id, PaymentMethod.BANK_TRANSFER, null, receipt, reference, buyer));
    }
}
