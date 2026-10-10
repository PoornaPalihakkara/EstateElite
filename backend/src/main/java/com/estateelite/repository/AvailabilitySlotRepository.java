package com.estateelite.repository;

import com.estateelite.model.AvailabilitySlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AvailabilitySlotRepository extends JpaRepository<AvailabilitySlot, Long> {
    List<AvailabilitySlot> findByAgentIdAndIsBookedFalse(Long agentId);
    List<AvailabilitySlot> findByAgentIdAndSlotDateTime(Long agentId, LocalDateTime slotDateTime);
    boolean existsByPropertyId(Long propertyId);
}
