package com.apitester.api_tester_backend.util;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class SensitiveDataMasker {

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "proxy-authorization",
            "cookie",
            "set-cookie",
            "x-api-key"
    );

    public Map<String, String> maskHeaders(
            Map<String, String> headers) {

        return headers.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> isSensitive(entry.getKey())
                                ? "******"
                                : entry.getValue()
                ));
    }

    private boolean isSensitive(String headerName) {
        return SENSITIVE_HEADERS.contains(
                headerName.toLowerCase());
    }
}