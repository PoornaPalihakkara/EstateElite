package com.estateelite.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agent_reports")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agent_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_agent_report_agent"))
    private User agent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_agent_report_reporter"))
    private User reporter;

    @Column(nullable = false, length = 1000)
    private String note;

    @Builder.Default
    @Column(nullable = false)
    private Boolean resolved = false;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (resolved == null) resolved = false;
    }
}
