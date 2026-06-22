package dev.matveev.metricsservice.service;

import dev.matveev.metricsservice.dto.MetricCreateReqDTO;
import dev.matveev.metricsservice.dto.MetricSummaryDTO;
import dev.matveev.metricsservice.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;

    public void saveMetric(MetricCreateReqDTO metricDTO, String clientId) {
        metricRepository.save(metricDTO.toEntity(clientId));
    }

    public List<MetricSummaryDTO> getSummaryForPeriod(OffsetDateTime from, OffsetDateTime to) {
        return metricRepository.getSummaryForPeriod(from, to);
    }
}
