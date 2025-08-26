package com.mastermind.services;

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
    private static final String INTEGER_GEN_URI = "https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new";

    private final HttpClient client;

    public RandomNumberApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    RandomNumberApiClient(HttpClient client) {
        this.client = client;
    }

    public NumCombination getRandomNums() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(INTEGER_GEN_URI))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Split body on every new line (Ref: https://www.random.org/clients/http/api/)
            String[] lines = response.body()
                    .trim()
                    .split(Pattern.quote("\n"));

            // String[] --> List<Integer>
            List<Integer> integerList = Arrays.stream(lines)
                    .map(Integer::parseInt)
                    .toList();

            return new NumCombination(integerList);

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
