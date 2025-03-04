package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import com.learnreactiveprogramming.exception.MovieException;
import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;
import java.time.Duration;
import java.util.List;

@Slf4j
public class MovieReactiveService {
    private MovieInfoService movieInfoService ;
    private ReviewService reviewService ;
    private RevenueService revenueService;

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> getAllMovies(){
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        return movieInfoFlux.flatMap(
                movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                }
        ).onErrorMap((ex) -> {
            log.error("Exception is " + ex);
            throw new MovieException(ex.getMessage());
        });
    }

    public Flux<Movie> getAllMovies_RestClient(){
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveAllMoviesInfo_RestClient();

        return movieInfoFlux.flatMap(
                movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux_RestClient(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                }
        ).onErrorMap((ex) -> {
            log.error("Exception is " + ex);
            throw new MovieException(ex.getMessage());
        });
    }

    public Flux<Movie> getAllMoviesRetry(){
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        return movieInfoFlux.flatMap(
                movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                }
        ).onErrorMap((ex) -> {
            log.error("Exception is " + ex);
            throw new MovieException(ex.getMessage());
        })
        .retry(3);
    }

    public Flux<Movie> getAllMoviesRetryWhen(){
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        Retry retryWhen = Retry.backoff(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));//throws orignal Exception -> MovieException

        return movieInfoFlux.flatMap(
                        movieInfo -> {
                            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                            return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                        }
                ).onErrorMap((ex) -> {
                    log.error("Exception is " + ex);
                    if(ex instanceof ServiceException) throw (ServiceException) ex;
                    else throw new MovieException(ex.getMessage());
                })
                .retryWhen(retryWhen);
    }

    public Flux<Movie> getAllMoviesRepeat(int repeatTimes){
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        Retry retryWhen = Retry.backoff(3, Duration.ofMillis(500))
                .filter(ex -> ex instanceof MovieException)
                .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) -> Exceptions.propagate(retrySignal.failure())));//throws orignal Exception -> MovieException

        return movieInfoFlux.flatMap(
                        movieInfo -> {
                            Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                            return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                        }
                ).onErrorMap((ex) -> {
                    log.error("Exception is " + ex);
                    if(ex instanceof ServiceException) throw (ServiceException) ex;
                    else throw new MovieException(ex.getMessage());
                })
                .retryWhen(retryWhen)
                .repeat(repeatTimes);
    }

    public Mono<Movie> getMovieById(int movieInfoId){
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieInfoId);
        Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfoId).collectList();
//        return movieInfoMono.flatMap(
//                movieInfo -> {
//                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
//                    return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
//                }
//        );
        return movieInfoMono.zipWith(reviewsMono, Movie::new);
    }

    public Mono<Movie> getMovieById_RestClient(int movieInfoId){
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMoviesInfoById_RestClient(movieInfoId);
        Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux_RestClient(movieInfoId).collectList();
        return movieInfoMono.zipWith(reviewsMono, Movie::new);
    }

    public Mono<Movie> getMovieByIdWithRevenue(int movieInfoId){
        Mono<MovieInfo> movieInfoMono = movieInfoService.retrieveMovieInfoMonoUsingId(movieInfoId);
        Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfoId).collectList();
        var revenue = revenueService.getRevenue(movieInfoId);
        var revenueMono = Mono.fromCallable(() -> revenue).subscribeOn(Schedulers.boundedElastic()); //wrappen um subscribeOn verwenden zu können
        return movieInfoMono.zipWith(reviewsMono, Movie::new)
                .zipWith(revenueMono, (movie, revenueItem) -> {movie.setRevenue(revenueItem); return movie;});
    }
}
