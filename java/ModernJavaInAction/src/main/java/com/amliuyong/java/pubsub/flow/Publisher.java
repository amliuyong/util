package com.amliuyong.java.pubsub.flow;


import java.util.concurrent.Flow;

public class Publisher {
    public static void main(String[] args) {
        //getTemperatures("New York").subscribe(new TempSubscriber());
        //TempInfo.fetch("New York").subscribe(new TempSubscriber());
        getCelsiusTemperatures("new york").subscribe(new TempSubscriber());
    }

    private static Flow.Publisher<TempInfo> getTemperatures(String town) {
        return subscriber -> subscriber.onSubscribe(
                new TempSubscription(subscriber, town));
    }

    public static Flow.Publisher<TempInfo> getCelsiusTemperatures(String town) {
        return subscriber -> {
            TempProcessor processor = new TempProcessor();
            processor.subscribe( subscriber );
            processor.onSubscribe( new TempSubscription(processor, town) );
        };
    }

}

