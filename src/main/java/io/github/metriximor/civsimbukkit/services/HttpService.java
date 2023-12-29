package io.github.metriximor.civsimbukkit.services;

import io.github.metriximor.civsimbukkit.mappers.Deserializer;
import io.github.metriximor.civsimbukkit.mappers.Serializable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class HttpService {
    private final Logger logger;
    private final HttpClient httpClient;

    public enum HttpMethod {
        POST,
        GET
    }

    public <I extends Serializable<I>, O> Optional<O> makeRequest(
            @NonNull final HttpMethod method,
            @NonNull final String url,
            @NonNull final Deserializer<O> deserializer,
            @NonNull final I requestBody) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(new URI(url));

            switch (method) {
                case POST:
                    String jsonRequestBody = requestBody.serialize();
                    requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody));
                    break;
                case GET:
                    requestBuilder.GET();
                    break;
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.warning("Failed response: %s".formatted(response));
                return Optional.empty();
            }

            return deserializer.deserialize(response.body());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.severe("Failed to make request: %s".formatted(e));
            return Optional.empty();
        }
    }
}
