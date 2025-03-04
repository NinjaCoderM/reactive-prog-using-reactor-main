package com.learnreactiveprogramming;

import jdk.dynalink.linker.ConversionComparator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

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
}
