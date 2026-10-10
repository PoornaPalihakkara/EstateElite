package com.estateelite.repository;

import com.estateelite.model.Property;
import com.estateelite.model.PropertyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findBySellerId(Long sellerId);
    List<Property> findByStatus(PropertyStatus status);
}
