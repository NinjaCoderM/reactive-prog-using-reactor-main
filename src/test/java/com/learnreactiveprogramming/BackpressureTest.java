package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BackpressureTest {
    @Test
    public void testBackpressure() {
        var numberRange = Flux.range(1, 100).log();

        //numberRange.subscribe(num -> log.info("Number: {}", num));
        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                //super.hookOnNext(value);
                log.info("hookOnNext: {}", value);
                if(value==2) cancel();
            }

            @Override
            protected void hookOnComplete() {
                //super.hookOnComplete();

            }

            @Override
            protected void hookOnError(Throwable throwable) {
                //super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                //super.hookOnCancel();
                log.info("hookOnCancel");
            }
        });
    }
    @Test
    public void testBackpressure1() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        //numberRange.subscribe(num -> log.info("Number: {}", num));
        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                //super.hookOnNext(value);
                log.info("hookOnNext: {}", value);
                if(value%2==0) request(2);
                else if (value > 50) cancel();
            }

            @Override
            protected void hookOnComplete() {
                //super.hookOnComplete();

            }

            @Override
            protected void hookOnError(Throwable throwable) {
                //super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                //super.hookOnCancel();
                log.info("hookOnCancel");
                latch.countDown();
            }
        });
        Assertions.assertTrue(latch.await(5L, TimeUnit.SECONDS) );
    }
    @Test
    public void testBackpressure_drop() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();

        CountDownLatch latch = new CountDownLatch(1);
        //numberRange.subscribe(num -> log.info("Number: {}", num));
        numberRange.onBackpressureDrop(item -> {
                    log.info("Dropped items: {}", item);
                })
                .subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                //super.hookOnNext(value);
                log.info("hookOnNext: {}", value);
//                if(value%2==0) request(2);
//                else if (value > 50) cancel(); //Without dropped items in logs
                if(value==2) hookOnCancel(); //Dropped items are in logs
            }

            @Override
            protected void hookOnComplete() {
                //super.hookOnComplete();

            }

            @Override
            protected void hookOnError(Throwable throwable) {
                //super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                //super.hookOnCancel();
                log.info("hookOnCancel");
                latch.countDown();
            }
        });
        Assertions.assertTrue(latch.await(5L, TimeUnit.SECONDS) );
    }
}
