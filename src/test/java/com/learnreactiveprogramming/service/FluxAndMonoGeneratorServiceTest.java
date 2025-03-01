package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

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
    void nameMonoMapFilter_switchDefault(){
        //given
        int minLengthName = 5;
        //when
        var nameMono = fluxAndMonoSchedulersService.namesMono_map_filter_switchIfDefault(minLengthName);
        //then
        StepVerifier.create(nameMono)
                .expectNext("DEFAULT")
                .as("Should return DEFAULT")
                .verifyComplete();
    }

    @Test
    void nameMonoMapFilter_whenResultIsDefault(){
        //given
        int minLengthName = 5;
        //when
        var nameMono = fluxAndMonoSchedulersService.namesMono_map_filter(minLengthName);
        //then
        StepVerifier.create(nameMono)
                .expectNext("default")
                .as("Should return default")
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

    @Test
    void namesMono_flatmap() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoSchedulersService.namesMono_flatmap(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();

    }

    @Test
    void namesMono_flatMapMany() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoSchedulersService.namesMono_flatMapMany(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();

    }

    @Test
    void namesFlux_namesFlux_transform() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoSchedulersService.namesFlux_transform(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_namesFlux_transform_switchIfEmpty() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoSchedulersService.namesFlux_transform_switchIfEmpty(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "L", "E", "X")
                .expectNextCount(5)
                .verifyComplete();

    }

    @Test
    void namesFlux_exploreConcat() {
        //given

        //when
        var concatFlux = fluxAndMonoSchedulersService.exploreConcat();


        //then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void namesFlux_exploreConcatWith() {
        //given

        //when
        var concatFlux = fluxAndMonoSchedulersService.exploreConcatWith();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void namesFlux_exploreConcatWithMono() {
        //given

        //when
        var concatFlux = fluxAndMonoSchedulersService.exploreConcatWithMono();

        //then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C")
                .verifyComplete();
    }

    @Test
    void explore_merge() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_merge();

        //then
        StepVerifier.create(value)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();

    }

    @Test
    void explore_mergeWith() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_mergeWith();

        //then
        StepVerifier.create(value)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();

    }

    @Test
    void explore_mergeWith_mono() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_mergeWith_mono();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B")
                .verifyComplete();

    }

    @Test
    void explore_mergeSequential() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_mergeSequential();

        //then
        StepVerifier.create(value)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

    }

    @Test
    void explore_zip() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_zip().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

    }

    @Test
    void explore_zip_1() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_zip_1().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();

    }

    @Test
    void explore_zipWith() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_zipWith().log();

        //then
        StepVerifier.create(value)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

    }

    @Test
    void explore_zipWith_mono() {

        //given

        //when
        var value = fluxAndMonoSchedulersService.explore_zipWith_mono().log();

        //then
        StepVerifier.create(value)
                .expectNext("AB")
                .verifyComplete();

    }

    @Test
    void namesFlux_map() {

        //given
        int stringLength = 3;

        //when
        var namesFlux = fluxAndMonoSchedulersService.namesFlux_map(stringLength).log();

        //then
        StepVerifier.create(namesFlux)
                //.expectNext("ALEX", "BEN", "CHLOE")
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();

    }

    @Test
    void exception_flux(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.exception_flux().log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void exception_flux_1(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.exception_flux().log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C")
                .expectError()
                .verify();
    }

    @Test
    void exception_flux_2(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.exception_flux().log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C")
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    @Test
    void explore_onErrorReturn(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.explore_onErrorReturn().log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C", "D", "F")
                .verifyComplete();
    }

    @Test
    void explore_onErrorResume(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.explore_onErrorResume(new IllegalStateException("Error Occurred")).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C", "D", "E", "F", "H")
                .verifyComplete();
    }

    @Test
    void explore_onErrorResume_1(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.explore_onErrorResume(new RuntimeException("Error Occurred")).log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C")
                .expectError()
                .verify();
    }

    @Test
    void explore_onErrorContinue(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.explore_onErrorContinue().log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "C", "D")
                .verifyComplete();
    }
    @Test
    void explore_onErrorMap(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.explore_onErrorMap().log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A")
                .expectError(ServiceException.class)
                .verify();
    }

    @Test
    void explore_doOnError(){
        //given

        //when
        var namesFlux = fluxAndMonoSchedulersService.explore_doOnError().log();

        //then
        StepVerifier.create(namesFlux)
                .expectNext("A", "B", "C")
                .expectError()
                .verify();
    }

    @Test
    void exception_mono_onErrorReturn() {

        //given


        //when
        var mono = fluxAndMonoSchedulersService.exception_mono_onErrorReturn();

        //then
        StepVerifier.create(mono)
                .expectNext("abc")
                .verifyComplete();
    }




}
