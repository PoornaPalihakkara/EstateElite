package com.estateelite.repository;

import com.estateelite.model.Appointment;
import com.estateelite.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAgentId(Long agentId);
    List<Appointment> findByBuyerId(Long buyerId);
    List<Appointment> findByAgentIdAndSlotDateTimeAndStatus(Long agentId, LocalDateTime slotDateTime, AppointmentStatus status);
    boolean existsByPropertyId(Long propertyId);
}
