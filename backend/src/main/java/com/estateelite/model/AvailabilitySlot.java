package com.estateelite.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "availability_slots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilitySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_slot_agent"))
    private User agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id",
            foreignKey = @ForeignKey(name = "fk_slot_property"))
    private Property property;

    @Column(nullable = false)
    private LocalDateTime slotDateTime;

    @Builder.Default
    private Boolean isBooked = false;
}
