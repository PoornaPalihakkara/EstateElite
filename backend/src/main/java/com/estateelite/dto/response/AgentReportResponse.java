package com.estateelite.dto.response;

import com.estateelite.model.AgentReport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentReportResponse {
    private Long id;
    private Long agentId;
    private String agentName;
    private String agentEmail;
    private String agentBranch;
    private Long reporterId;
    private String reporterName;
    private String note;
    private Boolean resolved;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public static AgentReportResponse from(AgentReport r) {
        return AgentReportResponse.builder()
                .id(r.getId())
                .agentId(r.getAgent().getId())
                .agentName(r.getAgent().getFullName())
                .agentEmail(r.getAgent().getEmail())
                .agentBranch(r.getAgent().getBranch())
                .reporterId(r.getReporter().getId())
                .reporterName(r.getReporter().getFullName())
                .note(r.getNote())
                .resolved(r.getResolved())
                .createdAt(r.getCreatedAt())
                .resolvedAt(r.getResolvedAt())
                .build();
    }
}
