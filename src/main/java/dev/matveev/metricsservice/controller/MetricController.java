package dev.matveev.metricsservice.controller;

import dev.matveev.metricsservice.dto.MetricCreateReqDTO;
import dev.matveev.metricsservice.dto.MetricSummaryDTO;
import dev.matveev.metricsservice.service.MetricService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/metrics")
@Slf4j
@RequiredArgsConstructor
public class MetricController {


    private final MetricService metricService;

    @PostMapping
    public ResponseEntity<Void> addMetric(@Valid @RequestBody MetricCreateReqDTO metricDTO,
                                          @AuthenticationPrincipal String clientId) {
        metricService.saveMetric(metricDTO, clientId);
        return ResponseEntity.status(CREATED).build();
    }


    @GetMapping
    public ResponseEntity<List<MetricSummaryDTO>> getMetrics(@RequestParam OffsetDateTime from,
                                                             @RequestParam OffsetDateTime to) {
        List<MetricSummaryDTO> between = metricService.getSummaryForPeriod(from, to);
        return ResponseEntity.ok(between);
    }


}
