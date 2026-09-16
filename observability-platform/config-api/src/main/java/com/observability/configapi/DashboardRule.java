package com.observability.configapi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "dashboard_rules")
public class DashboardRule implements Serializable {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String metricName;
    private double threshold;

    public DashboardRule() {}

    public DashboardRule(String name, String metricName, double threshold) {
        this.name = name;
        this.metricName = metricName;
        this.threshold = threshold;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getMetricName() { return metricName; }
    public double getThreshold() { return threshold; }
}
