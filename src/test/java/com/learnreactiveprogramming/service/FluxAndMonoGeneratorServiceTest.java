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
}
