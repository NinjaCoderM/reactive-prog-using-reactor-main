package com.learnreactiveprogramming.service;

import com.learnreactiveprogramming.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public Flux<String> namesFlux(){
        return Flux.fromIterable(namesList);//.log();
    }

    public Flux<String> namesFlux_async_flatMap(){
        return Flux.fromIterable(namesList)
                .flatMap(this::split_withDelay).log();
    }

    public Flux<String> split_withDelay(String name){
        var delay = new Random().nextInt(500);
        return Flux.fromArray(name.split("")).delayElements(Duration.ofMillis(delay));
    }

    public Flux<String> namesFlux_sync_concatMap(){
        return Flux.fromIterable(namesList)
                .concatMap(this::split_withDelay).log();
    }

    public Mono<String> nameMono(){
        return Mono.just(namesList.getFirst());//.log();
    }

    public Mono<String> namesMono_map_filter (int minLengthName){
        Mono<String> monoVar =namesList.isEmpty()?Mono.empty(): Mono.just(namesList.getFirst());
        return monoVar
                .filter(name -> name.length() >= minLengthName)
                .map(String::toUpperCase)
                .defaultIfEmpty("default");
    }

    public Mono<String> namesMono_map_filter_switchIfDefault (int minLengthName){
        Mono<String> monoVar =namesList.isEmpty()?Mono.empty(): Mono.just(namesList.getFirst());
        Function<Mono<String>, Mono<String>> transform = name -> name
                .filter(s -> s.length() >= minLengthName)
                .map(String::toUpperCase);
        Mono<String> defaultVar = Mono.just("default")
                .transform(transform);
        return monoVar
                .transform(transform)
                .switchIfEmpty(defaultVar);
    }

    public Mono<List<String>> namesMono_flatmap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono); //Mono<List of A, L, E  X>
//              Wenn man .map(this::splitStringMono) statt .flatMap() benutzt, dann bekommst man ein
//              Mono<Mono<List<String>>>, also ein verschachteltes Mono – was nicht gewollt ist.
//              Was macht flatMap?
//              Es nimmt das innere Mono<List<String>> und gibt es direkt zurück, sodass man am Ende nur noch ein
//              Mono<List<String>> hat.
    }

    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString); //Mono<List of A, L, E  X>
    }

    public Flux<String> splitString(String name){
        return Flux.fromArray(name.split(""));
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        return Mono.just(List.of(charArray))
                .delayElement(Duration.ofSeconds(1));
    }

    public Flux<String> namesFlux_transform(int stringLength) {

        //e.g. operations used many times and should not be duplicated
        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                .transform(filterMap) // gives u the opportunity to combine multiple operations using a single call.
                .flatMap(this::splitString)
                .defaultIfEmpty("default");
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {

        Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitString);

        var defaultFlux = Flux.just("default")
                .transform(filterMap); //"D","E","F","A","U","L","T"

        var namesList = List.of("alex", "ben", "chloe"); // a, l, e , x
        return Flux.fromIterable(namesList)
                .transform(filterMap) // gives u the opportunity to combine multiple operations using a single call.
                .switchIfEmpty(defaultFlux);
        //using "map" would give the return type as Flux<Flux<String>

    }

    public Flux<String> exploreConcat(){
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.concat(abcFlux, defFlux);
    }

    public Flux<String> exploreConcatWith(){
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.concatWith( defFlux);
    }

    public Flux<String> exploreConcatWithMono(){
        var abcMono = Mono.just("A");
        var defFlux = Flux.just("B", "C");
        return abcMono.concatWith( defFlux);
    }

    public Flux<String> explore_merge() {
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> explore_mergeWith() {
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> explore_mergeWith_mono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");
        return aMono.mergeWith(bMono).log();
    }

    public Flux<String> explore_mergeSequential() {

        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));

        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));

        return Flux.mergeSequential(abcFlux, defFlux).log();

    }

    public Flux<String> explore_zip() {
        var abcFlux = Flux.just("A", "B", "C", "G");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second);
    }

    public Flux<String> explore_zip_1() {
        var abcFlux = Flux.just("A", "B", "C", "G");
        var defFlux = Flux.just("D", "E", "F");
        var oneFlux = Flux.just("1", "2", "3");
        var fourFlux = Flux.just("4", "5", "6");
        return Flux.zip(abcFlux, defFlux, oneFlux, fourFlux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4());
    }

    public Flux<String> explore_zipWith() {
        var abcFlux = Flux.just("A", "B", "C", "G");
        var defFlux = Flux.just("D", "E", "F");
        return abcFlux.zipWith(defFlux, (first, second) -> first + second);
    }

    public Mono<String> explore_zipWith_mono() {
        var aMono = Mono.just("A");
        var bMono = Mono.just("B");
        return aMono.zipWith(bMono, (first, second) -> first + second);
    }

    public Flux<String> namesFlux_map(int stringLength) {
        var namesList = List.of("alex", "ben", "chloe");
        //return Flux.just("alex", "ben", "chloe");

        //Flux.empty()
        return Flux.fromIterable(namesList)
                //.map(s -> s.toUpperCase())
                .map(String::toUpperCase)
                .delayElements(Duration.ofMillis(500))
                .filter(s -> s.length() > stringLength)
                .map(s -> s.length() + "-" + s)
                .doOnNext(name -> {
                    System.out.println("name is : " + name);
                    name = name.toLowerCase(); // no effect because side effect operator !!!!!
                })
                .doOnSubscribe(s -> {
                    System.out.println("Subscription  is : " + s);
                })
                .doOnComplete(() -> {
                    System.out.println("Completed sending all the items.");
                })
                .doFinally((signalType) -> {
                    System.out.println("value is : " + signalType);
                })
                .defaultIfEmpty("default");
    }


    public Flux<String> exception_flux(){
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"));
    }

    public Flux<String> explore_onErrorReturn(){
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("E"))
                .onErrorReturn("D")
                .concatWith(Flux.just("F"));
    }

    public Flux<String> explore_onErrorResume(Exception e){
        var recoveryFlux = Flux.just("D", "E", "F");
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e))
                .concatWith(Flux.just("G"))
                .onErrorResume(ex -> {
                    log.error("Exception is " + ex);
                    if(ex instanceof IllegalStateException){
                        return recoveryFlux;
                    } else {
                        return Flux.error(ex);
                    }

                })
                .concatWith(Flux.just("H"));
    }

    public Flux<String> explore_onErrorContinue(){
        return Flux.just("A", "B", "C")
                .flatMap(name -> {
                    if (name.equals("B")) {
                        return Mono.error(new IllegalStateException("B"));
                    }
                    return Mono.just(name);
                })
                .concatWith(Flux.just("D"))
                .onErrorContinue(IllegalStateException.class, /* Optional Recover only from IllegalStateException */(ex, name) -> {
                    log.error("Exception is " + ex);
                    log.info("name is {}", name);

                });
    }

    public Flux<String> explore_onErrorMap(){
        return Flux.just("A", "B", "C")
                .flatMap(name -> {
                    if (name.equals("B")) {
                        return Mono.error(new IllegalStateException("B"));
                    }
                    return Mono.just(name);
                })
                .concatWith(Flux.just("D"))
                .onErrorMap(IllegalStateException.class, /* Optional Recover only from IllegalStateException */(ex) -> {
                    log.error("Exception is " + ex);
                    return new ServiceException(ex);
                });
    }

    public Flux<String> explore_onErrorMap_Debug(Exception e){
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e)) // line is not listed in Stacktrace, instead return new ServiceException ist listed
                //.doOnError(ex -> log.error("Caught exception at this point: ", ex))
                //.checkpoint("Fehler bei Flux", true)
                .onErrorMap(IllegalStateException.class, /* Optional Recover only from IllegalStateException */(ex) -> {
                    log.error("Exception is" + ex);
                    return new ServiceException(ex);
                });
    }

    public Flux<String> explore_onErrorMap_Debug_checkpoint(Exception e){
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(e)) // line is not listed in Stacktrace, instead return new ServiceException ist listed
                //.doOnError(ex -> log.error("Caught exception at this point: ", ex))
                .checkpoint("Fehler bei Flux")
                .onErrorMap(IllegalStateException.class, /* Optional Recover only from IllegalStateException */(ex) -> {
                    log.error("Exception is" + ex);
                    return new ServiceException(ex);
                });
    }

    public Flux<String> explore_doOnError(){
        return Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("E"))
                .doOnError(RuntimeException.class, (e) -> { log.error("Exception is " + e); })
                .concatWith(Flux.just("F"));
    }

    public Mono<Object> exception_mono_onErrorReturn() {
        return Mono.just("B")
                .map(value -> {
                    throw new RuntimeException("Exception Occurred");
                }).onErrorReturn("abc");
    }

    public Mono<String> exception_mono_onErrorContinue(String input){
        return Mono.just(input)
                .map(value -> {
                      if(value.equals("abc"))throw new RuntimeException("Error Occurred " + value);
                      return value;
                })
                .onErrorContinue(RuntimeException.class, (ex, name) -> {
                    log.error("Exception is " + ex);
                    log.info("name is {}", name);
                });
    }

    public Flux<Integer> explore_generate(){
        return Flux.generate(() -> 1, (state, sink) -> {
            sink.next(state*2);
            if (state == 10) {
                sink.complete();
            }
            return state + 1;
        });
    }

    public static List<String> names(){
        delay(1000);
        return List.of("A", "B", "C");
    }

    public Flux<String> explore_create(){
        return Flux.create((sink) -> {
            //names().forEach(sink::next);
            CompletableFuture
                    .supplyAsync(() -> names())
                    .thenAccept(names -> {
                        names.forEach(sink::next);
                    })
                    .thenRun(()->sendEvents(sink));
        });
    }

    public void sendEvents(FluxSink<String> sink){

            CompletableFuture
                    .supplyAsync(() -> names())
                    .thenAccept(names -> {
                        names.forEach(sink::next);
                    })
                    .thenRun(sink::complete);

    }

    public Mono<String> explore_create_mono(){
        return Mono.create(sink -> {
            sink.success("ALEX");
        });
    }

    public Flux<String> explore_handle(){
        return Flux.fromIterable(namesList)
                .handle((name, sink) -> {
                    if (name.length() > 3) {
                        sink.next(name.toUpperCase());
                    }
                });
    }

    public static void main(String[] args) {
        FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();
        service.namesFlux().subscribe(name -> System.out.println("Name is: " + name));
        service.nameMono().subscribe(name -> System.out.println("Mono name is: " + name));
    }
}
