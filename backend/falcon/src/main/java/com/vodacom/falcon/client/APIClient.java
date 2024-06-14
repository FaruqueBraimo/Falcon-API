package com.vodacom.falcon.client;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class APIClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(APIClient.class);

    public static HttpResponse<String> getData(String url) {
        LOGGER.info("Getting data from {} url: ", url);

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
        return Failsafe.with(buildRetryPolicy()).get(() -> {
            HttpResponse<String> response = buildHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new Exception(String.format("Non-success response requesting: %s with status code: %s , body: %s", request.uri(), response.statusCode(), response.body()));
            }
            return response;
        });
    }

    private static RetryPolicy<HttpResponse<String>> buildRetryPolicy() {
        return RetryPolicy.<HttpResponse<String>>builder()
                .handle(Exception.class)
                .withDelay(Duration.ofSeconds(30))
                .withMaxRetries(5)
                .onRetry(event -> LOGGER.info("Retrying: {} attempt", event.getAttemptCount()))
                .onFailedAttempt(event -> LOGGER.warn("Failed attempt: {}", event.getAttemptCount()))
                .onSuccess(event -> LOGGER.info("Successful http call attempt"))
                .build();
    }

    private static HttpClient buildHttpClient() {
        return HttpClient
                .newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }
}
