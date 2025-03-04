package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {
    private WebClient webClient;
    MovieInfoService movieInfoService;
    ReviewService reviewService;
    RevenueService revenueService = new RevenueService();
    MovieReactiveService movieReactiveService;

    @BeforeEach
    void setUp() {
        webClient = WebClient.builder().baseUrl("http://localhost:8080/movies").build();
        movieInfoService = new MovieInfoService(webClient);
        reviewService = new ReviewService(webClient);
        movieReactiveService = new MovieReactiveService(movieInfoService, reviewService, revenueService);
    }

    @Test
    void getAllMovies() {
        Flux<Movie> allMovies = movieReactiveService.getAllMovies().log();
        StepVerifier.create(allMovies)
                .assertNext( movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName(), "Name of movie should be Batman Begins");
                    assertEquals(2, movie.getReviewList().size(), "Expect 2 reviews");
                })
                .assertNext( movie -> {
                    assertEquals("The Dark Knight", movie.getMovie().getName(), "Name of movie should be The Dark Knight");
                    assertEquals(2, movie.getReviewList().size(), "Expect 2 reviews");
                })
                .assertNext( movie -> {
                    assertEquals("Dark Knight Rises", movie.getMovie().getName(), "Name of movie should be batman begins");
                    assertEquals(2, movie.getReviewList().size(), "Expect 2 reviews");
                })
                .verifyComplete();
    }

    @Test
    void getAllMovies_RestClient() {
        Flux<Movie> allMovies = movieReactiveService.getAllMovies_RestClient().log();
        StepVerifier.create(allMovies)
                .expectNextCount(7)
                .verifyComplete();
    }

    @Test
    void getMovieById() {
        Mono<Movie> allMovies = movieReactiveService.getMovieById(1).log();
        StepVerifier.create(allMovies)
                .assertNext( movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName(), "Name of movie should be Batman Begins");
                    assertEquals(2, movie.getReviewList().size(), "Expect 2 reviews");
                })
                .verifyComplete();
    }

    @Test
    void getMovieById_RestClient() {
        Mono<Movie> allMovies = movieReactiveService.getMovieById_RestClient(1).log();
        StepVerifier.create(allMovies)
                .assertNext( movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName(), "Name of movie should be Batman Begins");
                    assertEquals(1, movie.getReviewList().size(), "Expect 1 reviews");
                })
                .verifyComplete();
    }

    @Test
    void getMovieByIdWithRevenue() {
        Mono<Movie> allMovies = movieReactiveService.getMovieByIdWithRevenue(1).log();
        StepVerifier.create(allMovies)
                .assertNext( movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName(), "Name of movie should be Batman Begins");
                    assertEquals(2, movie.getReviewList().size(), "Expect 2 reviews");
                    assertNotNull(movie.getRevenue());
                })
                .verifyComplete();
    }


}