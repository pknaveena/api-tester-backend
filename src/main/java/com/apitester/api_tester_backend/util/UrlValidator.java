package com.apitester.api_tester_backend.util;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.apitester.api_tester_backend.exception.ApiException;

@Component
public class UrlValidator {

    private static final Set<String> BLOCKED_HOSTS = Set.of(
            "localhost",
            "127.0.0.1",
            "0.0.0.0");

    public void validate(String url) {

        URI uri;

        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException e) {
            throw new ApiException("Invalid URL",
                    HttpStatus.BAD_REQUEST);
        }

        String scheme = uri.getScheme();

        if (!"http".equalsIgnoreCase(scheme)
                && !"https".equalsIgnoreCase(scheme)) {

            throw new ApiException(
                    "Only HTTP and HTTPS URLs are supported",
                    HttpStatus.BAD_REQUEST);
        }

        String host = uri.getHost();

        if (host == null || host.isBlank()) {
            throw new ApiException("Invalid URL host",
                    HttpStatus.BAD_REQUEST);
        }

        if (BLOCKED_HOSTS.contains(host.toLowerCase())) {

            throw new ApiException(
                    "Requests to internal hosts are not allowed", HttpStatus.FORBIDDEN);
        }

          validateResolvedAddress(host);
    }


    private void validateResolvedAddress(String host) {

        try {

            InetAddress address =
                    InetAddress.getByName(host);

            if (address.isLoopbackAddress()
                    || address.isAnyLocalAddress()
                    || address.isSiteLocalAddress()
                    || address.isLinkLocalAddress()) {

                throw new ApiException(
                        "Requests to internal addresses are not allowed",
                        HttpStatus.FORBIDDEN);
            }

        } catch (UnknownHostException e) {

            throw new ApiException(
                    "Unable to resolve target host",
                    HttpStatus.BAD_REQUEST);
        }
    }
}