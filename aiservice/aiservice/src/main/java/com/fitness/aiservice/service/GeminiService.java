package com.fitness.aiservice.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class GeminiService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    private final WebClient webClient;
    private static final AtomicInteger counter = new AtomicInteger();
    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }
    public String getAnswer (String question){

        Map<String,Object> requestBody=Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                           Map.of("text",question)
                })
                });
        // This i had for debugging and find out why gemini response was not working
//        System.out.println(
//                "Gemini Request Count = "
//                        + counter.incrementAndGet()
//        );
//        System.out.println("=================================");
//        System.out.println("Calling Gemini");
//        System.out.println("URL: " + geminiApiUrl);
//        System.out.println("Key Prefix: " + geminiApiKey.substring(0,10));
//        System.out.println("Question: " + question);
//        System.out.println("=================================");
        String response = webClient.post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        eresponse -> eresponse.bodyToMono(String.class)
                                .map(body -> {
                                    System.out.println("Gemini url "+geminiApiKey);
                                    System.out.println("Gemini Error Body: " + body);
                                    return new RuntimeException(body);
                                })
                )
                .bodyToMono(String.class)
                .block();


        return response;

    }
    }

