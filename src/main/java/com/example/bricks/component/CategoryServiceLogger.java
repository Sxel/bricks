package com.example.bricks.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CategoryServiceLogger {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceLogger.class);

    public void logRequestAttempt() {
        logger.info("Attempting to fetch categories from external service");
    }

    public void logRequestSuccess(int count) {
        logger.info("Successfully fetched {} categories from external service", count);
    }

    public void logRequestLimited() {
        logger.warn("Daily request limit reached. Using cached categories.");
    }

    public void logRequestFailure(Throwable t) {
        logger.error("Failed to fetch categories from external service", t);
    }
}