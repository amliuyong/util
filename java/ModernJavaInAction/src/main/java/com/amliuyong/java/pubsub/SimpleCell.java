package com.amliuyong.java.pubsub;

import java.util.ArrayList;
import java.util.List;

interface Subscriber<T> {
    void onNext(T newValue);
}

interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}

public class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
    private int value = 0;
    private String name;
    private List<Subscriber> subscribers = new ArrayList<>();

    public SimpleCell(String name) {
        this.name = name;
    }

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        subscribers.add(subscriber);
    }

    private void notifyAllSubscribers() {

        subscribers.forEach(subscriber -> subscriber.onNext(this.value));
    }

    @Override
    public void onNext(Integer newValue) {
        this.value = newValue;
        System.out.println(this.name + ":" + this.value);
        notifyAllSubscribers();
    }


}

class ArithmeticCell extends SimpleCell {
    private int left;
    private int right;

    public ArithmeticCell(String name) {
        super(name);
    }

    public void setLeft(int left) {
        this.left = left;
        onNext(left + this.right);
    }

    public void setRight(int right) {
        this.right = right;
        onNext(right + this.left);
    }


    public static void main(String... args) {
        SimpleCell C1 = new SimpleCell("C1");
        SimpleCell C2 = new SimpleCell("C2");
        ArithmeticCell C3 = new ArithmeticCell("C3");

        C1.subscribe(C3::setRight);
        C2.subscribe(C3::setLeft);
        C1.onNext(10);
        C2.onNext(20);
        C1.onNext(15);

    }
}