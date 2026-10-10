package com.estateelite.repository;

import com.estateelite.model.AgentReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentReportRepository extends JpaRepository<AgentReport, Long> {
    List<AgentReport> findByResolvedFalseOrderByCreatedAtDesc();
    List<AgentReport> findAllByOrderByCreatedAtDesc();
    List<AgentReport> findByAgentIdOrderByCreatedAtDesc(Long agentId);
}
