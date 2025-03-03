package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieReactiveServiceMockTest {

    private AutoCloseable mocks;

    @Mock
    MovieInfoService movieInfoService;

    @Mock
    ReviewService reviewService;

    @InjectMocks
    MovieReactiveService movieReactiveService;

//    @BeforeEach  statt  @ExtendWith(MockitoExtension.class)
//    void setUp() {
//        mocks = MockitoAnnotations.openMocks(this);
//    }
//
//    @AfterEach
//    void tearDown() throws Exception {
//        mocks.close();
//    }

    @Test
    void getAllMovies() {
        //given
        var movie = new MovieInfo(98L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenCallRealMethod();
        Mockito.when(reviewService.retrieveReviewsFlux(Mockito.anyLong())).thenCallRealMethod();
        Flux<Movie> allMovies = movieReactiveService.getAllMovies().log();

        //then
        StepVerifier.create(allMovies)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getAllMovies_whenExceptionThrown() {
        //given
        var movie = new MovieInfo(98L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.just(movie));
        Mockito.when(reviewService.retrieveReviewsFlux(movie.getMovieInfoId())).thenThrow(new MovieException("TEST"));
        Flux<Movie> allMovies = movieReactiveService.getAllMovies().log();

        //then
        StepVerifier.create(allMovies)
                .expectError(MovieException.class)
                .verify();
    }



    @Test
    void getAllMovies_whenExceptionThrown_retry() {
        //given
        var movie = new MovieInfo(98L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.just(movie));
        Mockito.when(reviewService.retrieveReviewsFlux(movie.getMovieInfoId())).thenThrow(new MovieException("TEST"));
        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRetry().log();

        //then
        StepVerifier.create(allMovies)
                .expectError(MovieException.class)
                .verify();
        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_whenExceptionThrown_retryWhen() {
        //given
        var movie = new MovieInfo(98L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.just(movie));
        Mockito.when(reviewService.retrieveReviewsFlux(movie.getMovieInfoId())).thenThrow(new MovieException("TEST"));
        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRetryWhen().log();

        //then
        StepVerifier.create(allMovies)
                .expectError(MovieException.class)
                .verify();
        verify(reviewService, times(4)).retrieveReviewsFlux(isA(Long.class));
    }

    @Test
    void getAllMovies_whenExceptionThrown_retryWhenFilter() {
        //given
        var movie = new MovieInfo(98L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.just(movie));
        Mockito.when(reviewService.retrieveReviewsFlux(movie.getMovieInfoId())).thenThrow(new ServiceException("TEST"));
        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRetryWhen().log();

        //then
        StepVerifier.create(allMovies)
                .expectError(ServiceException.class)
                .verify();
        verify(reviewService, times(1)).retrieveReviewsFlux(isA(Long.class));
    }
    @Test
    void getAllMoviesRepeat() {
        //given
        var movie = new MovieInfo(98L, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        int repeatTimes = 4;
        Mockito.when(movieInfoService.retrieveMoviesFlux()).thenReturn(Flux.just(movie));
        Mockito.when(reviewService.retrieveReviewsFlux(movie.getMovieInfoId())).thenCallRealMethod();
        Flux<Movie> allMovies = movieReactiveService.getAllMoviesRepeat(repeatTimes).log();

        //then
        StepVerifier.create(allMovies)
                .expectNextCount(repeatTimes+1)
                .verifyComplete();
        verify(reviewService, times(repeatTimes+1)).retrieveReviewsFlux(isA(Long.class));
    }
}