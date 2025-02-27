package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoSchedulersService fluxAndMonoSchedulersService = new FluxAndMonoSchedulersService();

    @Test
    @DisplayName("Test Flux")
    void testFlux() {
    //given

    //when
    var namesFlux = fluxAndMonoSchedulersService.namesFlux();

    //then
    StepVerifier.create(namesFlux)
            .expectNext("alex", "ben", "chloe")
            .as("Should return alex, ben, chloe")
            .verifyComplete();

    StepVerifier.create(namesFlux)
            .expectNextCount(3)
            .as("Should have exactly 3 items")
            .verifyComplete();

    StepVerifier.create(namesFlux)
            .expectNext("alex")
            .as("Should return alex")
            .expectNextCount(2)
            .as("Should have exactly 2 items")
            .verifyComplete();

    }

    @Test
    void nameMono() {
        //given

        //when
        var nameMono = fluxAndMonoSchedulersService.nameMono();

        //then
        StepVerifier.create(nameMono)
                .expectNext("alex")
                .as("Should return alex")
                .verifyComplete();

        StepVerifier.create(nameMono)
                .expectNextCount(1)
                .as("Should have exactly 1 items")
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilter(){
        //given
        int minLengthName = 3;
        //when
        var nameMono = fluxAndMonoSchedulersService.namesMono_map_filter(minLengthName);
        //then
        StepVerifier.create(nameMono)
                .expectNext("ALEX")
                .as("Should return ALEX")
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilter_whenResultIsEmpty(){
        //given
        int minLengthName = 5;
        //when
        var nameMono = fluxAndMonoSchedulersService.namesMono_map_filter(minLengthName);
        //then
        StepVerifier.create(nameMono)
                .expectNextCount(0)
                .as("Count should be 0")
                .verifyComplete();
    }
}
