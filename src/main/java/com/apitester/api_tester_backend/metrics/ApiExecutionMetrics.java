package com.apitester.api_tester_backend.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ApiExecutionMetrics {

    private final Counter totalExecutions;
    private final Counter successfulExecutions;
    private final Counter failedExecutions;

    public ApiExecutionMetrics(MeterRegistry meterRegistry) {

        totalExecutions = Counter.builder("api.execution.count")
                .description("Total number of API execution attempts")
                .register(meterRegistry);

        successfulExecutions = Counter.builder("api.execution.success")
                .description("Number of successful API executions")
                .register(meterRegistry);

        failedExecutions = Counter.builder("api.execution.failure")
                .description("Number of failed API executions")
                .register(meterRegistry);
    }

    public void incrementTotal() {
        totalExecutions.increment();
    }

    public void incrementSuccess() {
        successfulExecutions.increment();
    }

    public void incrementFailure() {
        failedExecutions.increment();
    }
}