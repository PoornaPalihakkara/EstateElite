package com.estateelite.service;

import com.estateelite.dto.request.FlagRequest;
import com.estateelite.dto.response.AgentReportResponse;
import com.estateelite.exception.BadRequestException;
import com.estateelite.exception.ResourceNotFoundException;
import com.estateelite.model.AgentReport;
import com.estateelite.model.NotificationType;
import com.estateelite.model.Role;
import com.estateelite.model.User;
import com.estateelite.repository.AgentReportRepository;
import com.estateelite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentReportService {

    private final AgentReportRepository agentReportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public AgentReportResponse reportAgent(Long agentId, FlagRequest request, User reporter) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
        if (agent.getRole() != Role.AGENT) {
            throw new BadRequestException("You can only report users with the AGENT role");
        }
        if (agent.getId().equals(reporter.getId())) {
            throw new BadRequestException("You cannot report yourself");
        }

        AgentReport report = AgentReport.builder()
                .agent(agent)
                .reporter(reporter)
                .note(request.getNote().trim())
                .resolved(false)
                .build();
        agentReportRepository.save(report);

        // Notify all admins would be ideal; notify the agent that a report was filed
        notificationService.notify(agent,
                "A report was filed against your profile and is under Admin review.",
                NotificationType.AGENT_REPORTED);

        return AgentReportResponse.from(report);
    }

    @Transactional(readOnly = true)
    public List<AgentReportResponse> getOpenReports() {
        return agentReportRepository.findByResolvedFalseOrderByCreatedAtDesc()
                .stream().map(AgentReportResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AgentReportResponse> getAllReports() {
        return agentReportRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(AgentReportResponse::from).collect(Collectors.toList());
    }

    @Transactional
    public AgentReportResponse resolve(Long reportId) {
        AgentReport report = agentReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent report not found"));
        report.setResolved(true);
        report.setResolvedAt(LocalDateTime.now());
        agentReportRepository.save(report);

        notificationService.notify(report.getReporter(),
                "Your report about agent " + report.getAgent().getFullName()
                        + " has been reviewed by an administrator.",
                NotificationType.GENERAL);

        return AgentReportResponse.from(report);
    }

    @Transactional
    public void deleteReport(Long reportId) {
        if (!agentReportRepository.existsById(reportId)) {
            throw new ResourceNotFoundException("Agent report not found");
        }
        agentReportRepository.deleteById(reportId);
    }
}
