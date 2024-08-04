package com.example.bricks.component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CategoryMetrics {

    private final Counter categoryRequestsCounter;
    private final Counter categoryRequestsLimitedCounter;

    public CategoryMetrics(MeterRegistry registry) {
        this.categoryRequestsCounter = Counter.builder("category_requests_total")
                .description("Total number of requests to the category service")
                .register(registry);

        this.categoryRequestsLimitedCounter = Counter.builder("category_requests_limited_total")
                .description("Total number of requests limited due to daily quota")
                .register(registry);
    }

    public void incrementRequestCount() {
        categoryRequestsCounter.increment();
    }

    public void incrementLimitedRequestCount() {
        categoryRequestsLimitedCounter.increment();
    }
}