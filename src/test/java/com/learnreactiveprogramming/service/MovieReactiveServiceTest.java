package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class MovieReactiveServiceTest {
    MovieInfoService movieInfoService = new MovieInfoService();
    ReviewService reviewService = new ReviewService();
    MovieReactiveService movieReactiveService = new MovieReactiveService(movieInfoService, reviewService);


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
    void getMovieById() {
        Mono<Movie> allMovies = movieReactiveService.getMovieById(1).log();
        StepVerifier.create(allMovies)
                .assertNext( movie -> {
                    assertEquals("Batman Begins", movie.getMovie().getName(), "Name of movie should be Batman Begins");
                    assertEquals(2, movie.getReviewList().size(), "Expect 2 reviews");
                })
                .verifyComplete();
    }
}