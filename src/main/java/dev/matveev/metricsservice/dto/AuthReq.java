package dev.matveev.metricsservice.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthReq(@NotBlank(message = "ClientID cannot be blank")
                      String clientID) {
}
