package com.estateelite.controller;

import com.estateelite.dto.request.FlagRequest;
import com.estateelite.dto.response.AgentReportResponse;
import com.estateelite.model.User;
import com.estateelite.service.AgentReportService;
import com.estateelite.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent-reports")
@RequiredArgsConstructor
public class AgentReportController {

    private final AgentReportService agentReportService;
    private final CurrentUserService currentUserService;

    @PostMapping("/agent/{agentId}")
    public ResponseEntity<AgentReportResponse> report(
            @PathVariable Long agentId,
            @Valid @RequestBody FlagRequest request) {
        User reporter = currentUserService.getCurrentUser();
        return ResponseEntity.ok(agentReportService.reportAgent(agentId, request, reporter));
    }

    @GetMapping("/open")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AgentReportResponse>> openReports() {
        return ResponseEntity.ok(agentReportService.getOpenReports());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AgentReportResponse>> allReports() {
        return ResponseEntity.ok(agentReportService.getAllReports());
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AgentReportResponse> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(agentReportService.resolve(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        agentReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
