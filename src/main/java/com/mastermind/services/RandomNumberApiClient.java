package com.mastermind.services;

import com.mastermind.config.GameConfig;
import com.mastermind.models.NumCombination;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RandomNumberApiClient {
    private static final String INTEGER_GEN_URI = "https://www.random.org/integers/?num=%d&min=0&max=%d&col=1&base=10&format=plain&rnd=new";

    private final HttpClient client;

    public RandomNumberApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    RandomNumberApiClient(HttpClient client) {
        this.client = client;
    }

    public NumCombination getRandomNums() throws RandomNumberApiException {
        return getRandomNums(GameConfig.DEFAULT_ANSWER_SIZE, GameConfig.DEFAULT_MAX_VALUE);
    }

    public NumCombination getRandomNums(int size, int max) throws RandomNumberApiException {
        String formattedString = INTEGER_GEN_URI.formatted(size, max);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(formattedString))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Check HTTP status code first
            if (response.statusCode() == 200) {
                // Success - parse the numbers
                return parseSuccessResponse(response.body());
            } else if (response.statusCode() == 503) {
                // Service unavailable - handle error response
                throw new RandomNumberApiException("Random.org API error: " + extractErrorMessage(response.body()));
            } else {
                // Other HTTP status codes
                throw new RandomNumberApiException("Unexpected HTTP status: " + response.statusCode() + " - " + response.body());
            }

        } catch (IOException | InterruptedException e) {
            throw new RandomNumberApiException("Network error while contacting Random.org API", e);
        }
    }

    private NumCombination parseSuccessResponse(String responseBody) {
        // Split body on every new line (Ref: https://www.random.org/clients/http/api/)
        String[] lines = responseBody
                .trim()
                .split(Pattern.quote("\n"));

        // String[] --> List<Integer>
        List<Integer> integerList = Arrays.stream(lines)
                .map(Integer::parseInt)
                .toList();

        return new NumCombination(integerList);
    }
    
    private String extractErrorMessage(String responseBody) {
        // Look for "Error:" prefix as per API docs
        if (responseBody != null && responseBody.startsWith("Error:")) {
            return responseBody;
        }
        return "Unknown error from Random.org API";
    }
}
