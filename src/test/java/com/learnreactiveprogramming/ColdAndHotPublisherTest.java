package com.learnreactiveprogramming;

import jdk.dynalink.linker.ConversionComparator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

public class ColdAndHotPublisherTest {
   @Test
   public void coldPublisherTest() {
       //given
       var flux = Flux.range(1,10);
       //when
       flux.subscribe(i -> System.out.println("Subscriber1: " + i));
       flux.subscribe(i -> System.out.println("Subscriber2: " + i));
       //then

   }
    @Test
    public void hotPublisherTest() {
        //given
        var flux = Flux.range(1,10).delayElements(Duration.ofSeconds(1));
        //when
        ConnectableFlux<Integer> connectableFlux = flux.publish();
        connectableFlux.connect();
        //then
        connectableFlux.subscribe(i -> System.out.println("Subscriber1: " + i));
        delay(4000);
        connectableFlux.subscribe(i -> System.out.println("Subscriber2: " + i));
        delay(10000);
    }

    @Test
    public void hotPublisherTest2() {
       //example from projectreactor.io Reference Guide
        Sinks.Many<String> hotSource = Sinks.unsafe().many().multicast().directBestEffort();

        Flux<String> hotFlux = hotSource.asFlux().map(String::toUpperCase);

        hotFlux.subscribe(d -> System.out.println("Subscriber 1 to Hot Source: "+d));

        hotSource.emitNext("blue", FAIL_FAST);
        hotSource.tryEmitNext("green").orThrow();

        hotFlux.subscribe(d -> System.out.println("Subscriber 2 to Hot Source: "+d));

        hotSource.emitNext("orange", FAIL_FAST);
        hotSource.emitNext("purple", FAIL_FAST);
        hotSource.emitComplete(FAIL_FAST);
    }

    @Test
    public void hotPublisherTest_autoConnect() {
        //given
        var flux = Flux.range(1,10).delayElements(Duration.ofSeconds(1));
        //when
        var hotSource = flux.publish().autoConnect(2);
        //then
        hotSource.subscribe(i -> System.out.println("Subscriber1: " + i));
        delay(4000);
        hotSource.subscribe(i -> System.out.println("Subscriber2: " + i));
        System.out.println("Second Subscriber is connected");
        delay(3000);
        hotSource.subscribe(i -> System.out.println("Subscriber3: " + i));
        System.out.println("Third Subscriber is connected");
        delay(10000);
    }
}
