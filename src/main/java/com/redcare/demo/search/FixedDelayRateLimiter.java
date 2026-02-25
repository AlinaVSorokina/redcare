package com.redcare.demo.search;

import com.redcare.demo.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A very small rate limiter that waits a fixed delay between requests.
 * <p>
 * This is primarily used to reduce the probability of hitting GitHub's secondary rate limits.
 */
@Component
public class FixedDelayRateLimiter implements RateLimiter {

    private final long delayMillis;

    public FixedDelayRateLimiter(@Value("${github.rateLimit.delayMillis:1000}") long delayMillis) {
        this.delayMillis = Math.max(0, delayMillis);
    }

    @Override
    public void acquire() {
        if (delayMillis <= 0) {
            return;
        }

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("unexpected failure");
        }
    }
}
