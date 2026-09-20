package com.apitester.api_tester_backend.ratelimit;
import lombok.Getter;

@Getter
public class UserRequestCounter {

    private long windowStart;
    private int requestCount;

    public UserRequestCounter() {
        this.windowStart = System.currentTimeMillis();
        this.requestCount = 0;
    }

    public void increment() {
        requestCount++;
    }

    public void reset(long currentTime) {
        windowStart = currentTime;
        requestCount = 0;
    }
}