package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ReviewServiceTest {
    WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/movies").build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);
    @Test
    void retrieveReviewsFlux_RestClient() {
        //given
        var movieId = 7;
        //when
        var review = new ReviewService(webClient).retrieveReviewsFlux_RestClient(movieId).log();
        //then
        assertNotNull(review);
        StepVerifier.create(review)
                .assertNext(reviewItem -> {
                    Assertions.assertEquals("The ending made all 22 movies worth it", reviewItem.getComment(), "Text should be the same");
                })
                .verifyComplete();
    }
}