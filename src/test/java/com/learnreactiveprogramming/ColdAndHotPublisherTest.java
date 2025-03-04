package com.learnreactiveprogramming;

import jdk.dynalink.linker.ConversionComparator;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

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

}
