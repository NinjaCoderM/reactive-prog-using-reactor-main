package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerServiceTest {

    SchedulerService schedulerService = new SchedulerService();

    @Test
    void explore_publishOn(){
        //given
        //when
        var flux = schedulerService.explore_publishOn().log();
        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_subscribeOn(){
        //given
        //when
        var flux = schedulerService.explore_subscribeOn().log();
        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    void explore_parallel(){
        //given
        //when
        var flux = schedulerService.explore_parallel().log();
        //then
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void explore_parallel_usingFlatMap(){
        //given
        //when
        var flux = schedulerService.explore_parallel_usingFlatMap().log();
        //then
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }
    @Test
    void explore_parallel_usingFlatMap_2(){
        //given
        //when
        var flux = schedulerService.explore_parallel_usingFlatMap_2().log();
        //then
        StepVerifier.create(flux)
                .expectNextCount(6)
                .verifyComplete();
    }
    @Test
    void explore_parallel_usingFlatMapSequential(){
        //given
        //when
        var flux = schedulerService.explore_parallel_usingFlatMapSequential().log();
        //then
        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }
}