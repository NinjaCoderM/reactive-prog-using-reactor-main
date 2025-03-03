package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class SchedulerService {
    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public Flux<String> explore_publishOn(){
        var namesFlux = Flux.fromIterable(namesList)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase);

        var namesFlux1 = Flux.fromIterable(namesList1)
                .publishOn(Schedulers.parallel())
                .map(this::upperCase);

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> explore_subscribeOn(){
        var namesFlux = getExternalData() //publishOn is not possible because External Data
                .subscribeOn(Schedulers.parallel());

        var namesFlux1 = Flux.fromIterable(namesList1)
                .map(this::upperCase)
                .subscribeOn(Schedulers.parallel());

        return namesFlux.mergeWith(namesFlux1);
    }

    private Flux<String> getExternalData() {
        return Flux.fromIterable(namesList)
                .map(this::upperCase);
    }
}
