package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.domain.Movie;
import com.learnreactiveprogramming.domain.MovieInfo;
import com.learnreactiveprogramming.domain.Review;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public class MovieReactiveService {
    private MovieInfoService movieInfoService ;
    private ReviewService reviewService ;
    private RevenueService revenueService = new RevenueService();

    public MovieReactiveService(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> getAllMovies(){
        Flux<MovieInfo> movieInfoFlux = movieInfoService.retrieveMoviesFlux();

        return movieInfoFlux.flatMap(
                movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.retrieveReviewsFlux(movieInfo.getMovieInfoId()).collectList();
                    return reviewsMono.map(reviews -> new Movie(movieInfo, reviews));
                }
        );
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
}
