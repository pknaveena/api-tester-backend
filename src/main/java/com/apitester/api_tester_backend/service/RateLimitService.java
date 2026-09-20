package com.apitester.api_tester_backend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.apitester.api_tester_backend.exception.ApiException;
import com.apitester.api_tester_backend.ratelimit.UserRequestCounter;

@Service
public class RateLimitService {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MILLIS = 60_000;

    // don't use this in production-grade distributed rate limiter.
    private final Map<String, UserRequestCounter> counters = new ConcurrentHashMap<>();

    public void checkRateLimit(String userKey) {

        long currentTime = System.currentTimeMillis();

        UserRequestCounter counter =
                counters.computeIfAbsent(userKey, key -> new UserRequestCounter());

        synchronized (counter) {

            if (currentTime - counter.getWindowStart()>= WINDOW_MILLIS) {
                counter.reset(currentTime);
            }

            if (counter.getRequestCount() >= MAX_REQUESTS) {
                throw new ApiException(
                        "Too many requests. Please try again later.",
                        HttpStatus.TOO_MANY_REQUESTS);
            }

            counter.increment();
        }
    }
}