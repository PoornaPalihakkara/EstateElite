package com.estateelite.repository;

import com.estateelite.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByBuyerId(Long buyerId);
    List<Transaction> findByPropertyId(Long propertyId);
    Optional<Transaction> findByInvoiceNumber(String invoiceNumber);
    boolean existsByPropertyId(Long propertyId);
}
