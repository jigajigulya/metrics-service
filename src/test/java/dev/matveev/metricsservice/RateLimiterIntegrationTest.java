package dev.matveev.metricsservice;

import dev.matveev.metricsservice.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class RateLimiterIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен блокировать 11-й запрос статусом 429 при превышении лимита клиента")
    public void shouldBlockReq11After10() throws Exception {

        String clientId = "test-attacker-client";
        String token = jwtService.generateToken(clientId);


        Map<String, Object> body = Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "value", 45.2,
                "payload", Map.of("status", "test")
        );
        String jsonBody = objectMapper.writeValueAsString(body);


        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/metrics")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isCreated());
        }


        mockMvc.perform(post("/metrics")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().is4xxClientError());
    }
}
