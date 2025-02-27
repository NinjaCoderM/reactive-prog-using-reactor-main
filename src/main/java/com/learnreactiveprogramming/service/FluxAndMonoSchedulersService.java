package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

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

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        return Mono.just(List.of(charArray))
                .delayElement(Duration.ofSeconds(1));
    }

    public static void main(String[] args) {
        FluxAndMonoSchedulersService service = new FluxAndMonoSchedulersService();
        service.namesFlux().subscribe(name -> System.out.println("Name is: " + name));
        service.nameMono().subscribe(name -> System.out.println("Mono name is: " + name));
    }
}
