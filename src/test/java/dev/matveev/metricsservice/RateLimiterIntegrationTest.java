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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


public class RateLimiterIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен блокировать 11-й запрос статусом 429 при превышении лимита клиента")
    public void shouldBlockReqAfter10() throws Exception {

        String clientId = "concurrent-attacker-client";
        String token = jwtService.generateToken(clientId);

        Map<String, Object> body = Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "value", 100.5,
                "payload", Map.of("env", "test")
        );
        String jsonBody = objectMapper.writeValueAsString(body);

        int totalRequests = 15; // Отправляем 15 одновременных запросов (лимит клиента — 10)
        ExecutorService executorService = Executors.newFixedThreadPool(totalRequests);
        CountDownLatch startLatch = new CountDownLatch(1); // Защелка для одновременного старта всех потоков
        CountDownLatch finishLatch = new CountDownLatch(totalRequests); // Защелка ожидания завершения всех потоков


        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger blockedCount = new AtomicInteger(0);

        for (int i = 0; i < totalRequests; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();


                    int statusCode = mockMvc.perform(post("/metrics")
                                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(jsonBody))
                            .andReturn()
                            .getResponse()
                            .getStatus();

                    if (statusCode == 201) {
                        successCount.incrementAndGet();
                    } else if (statusCode == 429) {
                        blockedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finishLatch.countDown(); // Поток завершил работу
                }
            });
        }


        startLatch.countDown();


        finishLatch.await();
        executorService.shutdown();
        assertEquals(10, successCount.get(), "Ровно 10 запросов должны вернуть статус 201");
        assertEquals(5, blockedCount.get(), "Остальные 5 запросов должны вернуть статус 429");
    }



}
