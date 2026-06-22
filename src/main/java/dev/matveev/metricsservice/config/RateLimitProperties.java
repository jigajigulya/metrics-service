package dev.matveev.metricsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(
        LimitConfig global,
        LimitConfig client,
        Duration redisTtl

) {
    public record LimitConfig(
            long capacity,
            Duration duration
    ) {}
}
