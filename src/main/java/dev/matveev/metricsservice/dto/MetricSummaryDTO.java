package dev.matveev.metricsservice.dto;

import java.math.BigDecimal;

public record MetricSummaryDTO(long count, Double avg, Double min, Double max) {}
