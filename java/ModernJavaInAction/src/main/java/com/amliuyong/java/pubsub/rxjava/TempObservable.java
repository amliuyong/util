package com.amliuyong.java.pubsub.rxjava;

import com.amliuyong.java.pubsub.flow.TempInfo;
import io.reactivex.Observable;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

public class TempObservable {
    public static void main(String... args) throws InterruptedException {
        Observable<String> strings = Observable.just("first", "second");
        Observable<Long> onePerSec = Observable.interval(1, TimeUnit.SECONDS);

        strings.subscribe(System.out::println);
        strings.subscribe(str -> {
            String s = "[ " + str + " ]";
            System.out.println(s);
        });

        // onePerSec.subscribe(System.out::println);

        // onePerSec.subscribe(i -> System.out.println(TempInfo.fetch( "New York" )));

        // onePerSec.blockingSubscribe(i -> System.out.println(TempInfo.fetch("New York")));

        // getTemperature("New York").blockingSubscribe(new TempObserver());

        //getNegativeTemperature("Beijing").blockingSubscribe(new TempObserver());

        Observable<TempInfo> observable = getCelsiusTemperatures(
                "New York", "Chicago", "San Francisco", "Beijing");
        observable.blockingSubscribe(new TempObserver());

    }


    public static Observable<TempInfo> getTemperature(String town) {

        return Observable.create(emitter ->
                Observable.interval(1, TimeUnit.MILLISECONDS)
                        .subscribe(i -> {
                            if (!emitter.isDisposed()) {
                                if (i >= 50) {
                                    System.out.println("completed.");
                                    emitter.onComplete();
                                } else {
                                    try {
                                        emitter.onNext(TempInfo.fetch(town + " " + i));
                                    } catch (Exception e) {
                                        System.out.println("Error.");
                                        emitter.onError(e);
                                    }
                                }
                            }
                        })
        );
    }


    public static Observable<TempInfo> getCelsiusTemperature(String town) {
        return getTemperature(town)
                .map(temp -> new TempInfo(temp.getTown(),
                        (temp.getTemp() - 32) * 5 / 9));
    }


    public static Observable<TempInfo> getNegativeTemperature(String town) {
        return getCelsiusTemperature(town)
                .filter(temp -> temp.getTemp() < 0);
    }


    public static Observable<TempInfo> getCelsiusTemperatures(String... towns) {
        return Observable.merge(
                Arrays.stream(towns)
                        .map(TempObservable::getCelsiusTemperature)
                        .collect(toList())
        );
    }


}
