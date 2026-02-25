package com.redcare.demo.search;

/**
 * Simple rate limiter abstraction used to avoid triggering GitHub secondary rate limits.
 */
public interface RateLimiter {

    void acquire();
}
