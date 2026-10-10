package com.estateelite.repository;

import com.estateelite.model.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByBuyerId(Long buyerId);
    Optional<WishlistItem> findByBuyerIdAndPropertyId(Long buyerId, Long propertyId);
    boolean existsByPropertyId(Long propertyId);
}
