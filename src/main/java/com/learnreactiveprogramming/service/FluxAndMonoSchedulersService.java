package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

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
        return Mono.just(namesList.getFirst())
                .filter(name -> name.length() >= minLengthName)
                .map(String::toUpperCase);
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
                .flatMap(this::splitString);
    }

    public static void main(String[] args) {
        FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();
        service.namesFlux().subscribe(name -> System.out.println("Name is: " + name));
        service.nameMono().subscribe(name -> System.out.println("Mono name is: " + name));
    }
}
