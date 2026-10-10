package com.estateelite.repository;

import com.estateelite.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPropertyId(Long propertyId);
    List<Review> findByAgentId(Long agentId);
    List<Review> findByFlaggedTrue();
    List<Review> findByBuyerId(Long buyerId);
    boolean existsByPropertyId(Long propertyId);
}
