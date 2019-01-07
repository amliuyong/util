package com.amliuyong.java.util;

import com.amliuyong.java.util.lazy.LazyList;
import com.amliuyong.java.util.lazy.MyList;

import java.util.stream.IntStream;

public class PrimeNumber {

    static IntStream numbers() {
        return IntStream.iterate(2, n -> n + 1);
    }

    static int head(IntStream numbers) {
        return numbers.findFirst().getAsInt();
    }

    static IntStream tail(IntStream numbers) {
        return numbers.skip(1);
    }


    // Below code does not work
    // Remember from chapter 4 that after you call a terminal operation on
    // a stream, it’s consumed forever!
    static IntStream primes_X(IntStream numbers) {
        int head = head(numbers);
        return IntStream.concat(
                IntStream.of(head),
                primes_X(tail(numbers).filter(n -> n % head != 0))
        );

    }

    public static MyList<Integer> primes(MyList<Integer> numbers) {
        return new LazyList<>(
                numbers.head(),
                () -> primes(
                        numbers.tail().filter(n -> n % numbers.head() != 0)
                )
        );
    }


    static void printAll() {
        MyList<Integer> list = primes(LazyList.from(2));
        while (!list.isEmpty()) {
            System.out.println(list.head());
            list = list.tail();
        }
    }

}
