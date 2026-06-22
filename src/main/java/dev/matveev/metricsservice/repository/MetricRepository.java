package dev.matveev.metricsservice.repository;

import dev.matveev.metricsservice.dto.MetricSummaryDTO;
import dev.matveev.metricsservice.model.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;


public interface MetricRepository extends JpaRepository<Metric, Long> {

    @Query("""
        SELECT new dev.matveev.metricsservice.dto.MetricSummaryDTO(
            COUNT(m),
            cast(COALESCE(AVG(m.value), 0.0) as double ) ,
            cast(COALESCE(MIN(m.value), 0.0) as double ) ,
            cast(COALESCE(MAX(m.value), 0.0) as double) 
        )
        FROM Metric m
        WHERE m.timestamp >= :from AND m.timestamp <= :to
    """)
    List<MetricSummaryDTO> getSummaryForPeriod(@Param("from") OffsetDateTime from, @Param("to") OffsetDateTime to);
}
