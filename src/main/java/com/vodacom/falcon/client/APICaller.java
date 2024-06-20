package com.vodacom.falcon.client;

import dev.failsafe.Failsafe;
import dev.failsafe.RateLimiter;
import dev.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class APICaller {
    private static final Logger LOGGER = LoggerFactory.getLogger(APICaller.class);
    private static final List<Integer> UNKNOWN_CODE_ERRORS = List.of(400, 401, 403, 404, 408, 500, 504, 505);

    public static HttpResponse<String> getData(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            return getDataWithFaultTolerance(request);
        } catch (Exception ex) {
            LOGGER.error("Connection failed: {}", ex.getMessage(), ex);
        }
        return null;
    }

    private static HttpResponse<String> getDataWithFaultTolerance(HttpRequest request) {
        if (getRateLimit().tryAcquirePermit()) {
            return Failsafe.with(buildRetryPolicy()).get(() -> {
                HttpResponse<String> response = buildHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200 && !UNKNOWN_CODE_ERRORS.contains(response.statusCode())) {
                    throw new Exception(String.format("Error requesting resource: %s with status code: %s , body: %s", request.uri(), response.statusCode(), response.body()));
                }
                return response;
            });
        }

        return null;
    }

    private static RetryPolicy<HttpResponse<String>> buildRetryPolicy() {
        return RetryPolicy.<HttpResponse<String>>builder()
                .handle(Exception.class)
                .withBackoff(1, 5, ChronoUnit.SECONDS)
                .withMaxRetries(5)
                .onRetry(event -> LOGGER.info("Retry: {} ", event.getAttemptCount()))
                .onFailedAttempt(event -> LOGGER.warn("Error requesting: {}", event.getAttemptCount()))
                .build();
    }

    private static RateLimiter<Object> getRateLimit() {
        return RateLimiter.burstyBuilder(3, Duration.ofSeconds(10)).build();
    }

    private static HttpClient buildHttpClient() {
        return HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }
}
