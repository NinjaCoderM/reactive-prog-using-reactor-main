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


    @Test
    void namesFlux_async_flatMap() {
        //given

        //when
        var namesFlux_async_flatMap = fluxAndMonoSchedulersService.namesFlux_async_flatMap();
        //then

//         INFO reactor.Flux.FlatMap.1 -- onNext(b)
//         INFO reactor.Flux.FlatMap.1 -- onNext(c)
//         INFO reactor.Flux.FlatMap.1 -- onNext(a)
//         INFO reactor.Flux.FlatMap.1 -- onNext(h)
//         INFO reactor.Flux.FlatMap.1 -- onNext(e)
//         INFO reactor.Flux.FlatMap.1 -- onNext(l)
//         INFO reactor.Flux.FlatMap.1 -- onNext(l)
//         INFO reactor.Flux.FlatMap.1 -- onNext(n)
//         INFO reactor.Flux.FlatMap.1 -- onNext(e)
//         INFO reactor.Flux.FlatMap.1 -- onNext(o)
//         INFO reactor.Flux.FlatMap.1 -- onNext(x)
//         INFO reactor.Flux.FlatMap.1 -- onNext(e)

        StepVerifier.create(namesFlux_async_flatMap)
                //.expectNext("a","l","e","x")
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    void namesFlux_sync_concatMap() {
        //given

        //when
        var namesFlux_async_flatMap = fluxAndMonoSchedulersService.namesFlux_sync_concatMap();
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(a)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(l)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(e)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(x)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(b)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(e)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(n)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(c)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(h)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(l)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(o)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onNext(e)
        //INFO reactor.Flux.ConcatMapNoPrefetch.1 -- onComplete()

        //then
        StepVerifier.create(namesFlux_async_flatMap)
                .expectNext("a","l","e","x")
                .expectNextCount(8)
                .verifyComplete();
    }
}
