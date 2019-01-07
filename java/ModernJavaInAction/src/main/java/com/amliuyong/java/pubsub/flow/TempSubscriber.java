package com.amliuyong.java.pubsub.flow;

import java.util.concurrent.Flow.*;

public class TempSubscriber implements Subscriber<TempInfo> {
    private Subscription subscription;
    private int n = 3;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(TempInfo tempInfo) {
        System.out.println(tempInfo);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        System.err.println(t.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println("Done!");
    }
}
