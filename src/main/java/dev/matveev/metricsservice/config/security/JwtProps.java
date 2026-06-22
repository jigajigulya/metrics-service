package dev.matveev.metricsservice.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProps(
        String secret,
        long expirationHours) {

}
