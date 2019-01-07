package com.amliuyong.java.util;

import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Fibonacci {
    public static void main(String... args) {
        getFibonacci_1(10);
        getFibonacci_2(10);
        getFibonacci_3(10);

    }

    public static void getFibonacci_1(int n) {
        Stream.iterate(new int[]{0, 1},
                t -> new int[]{t[1], t[0] + t[1]})
                .limit(n)
                .forEach(t -> System.out.println("(" + t[0] + "," + t[1] + ")"));
    }

    public static void getFibonacci_2(int n) {

        Stream.iterate(new int[]{0, 1},
                t -> new int[]{t[1], t[0] + t[1]})
                .limit(n)
                .map(t -> t[0])
                .forEach(System.out::println);

    }


    public static void getFibonacci_3(int n) {

        IntSupplier fib = new IntSupplier() {
            private int previous = 0;
            private int current = 1;

            public int getAsInt() {
                int oldPrevious = this.previous;
                int nextValue = this.previous + this.current;
                this.previous = this.current;
                this.current = nextValue;
                return oldPrevious;
            }
        };
        IntStream.generate(fib).limit(n).forEach(System.out::println);


    }

}
