package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieInfoServiceTest {

    WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080/movies").build();

    MovieInfoService movieInfoService = new MovieInfoService(webClient);
    @Test
    void retrieveAllMoviesInfo_RestClient() {
        //given
        //when
        var movieInfoFlux = movieInfoService.retrieveAllMoviesInfo_RestClient().log();
        //then
        assertNotNull(movieInfoFlux);
        StepVerifier.create(movieInfoFlux).expectNextCount(7).verifyComplete();
    }
    @Test
    void retrieveMoviesInfoById_RestClient() {
        //given
        long id = 7;
        //when
        var movieInfoMono = movieInfoService.retrieveMoviesInfoById_RestClient(id).log();
        //then
        assertNotNull(movieInfoMono);
        StepVerifier.create(movieInfoMono).expectNextCount(1).verifyComplete();
    }
}