package com.observability.configapi;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DashboardRuleRepository extends JpaRepository<DashboardRule, UUID> {
}
