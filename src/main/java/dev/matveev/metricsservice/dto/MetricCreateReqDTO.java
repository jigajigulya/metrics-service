package dev.matveev.metricsservice.dto;

import dev.matveev.metricsservice.model.Metric;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

public record MetricCreateReqDTO(
        @NotNull(message = "Timestamp is required")
        OffsetDateTime timestamp,

        @NotNull(message = "Value is required")
        @Positive(message = "Value must be strictly greater than 0")
        Double value,

        @NotNull(message = "Payload is required")
        Map<String, Object> payload
) {
    public Metric toEntity(String clientId) {
        Metric metric = new Metric();
        metric.setTimestamp(timestamp);
        metric.setValue(value);
        metric.setClientId(clientId);
        return metric;
    }
}
