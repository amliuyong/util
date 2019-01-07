package com.amliuyong.java.util.lazy;

public class Main {
    public static void main(String... args) {
        LazyList<Integer> numbers = LazyList.from(2);
        int two = numbers.head();
        int three = numbers.tail().head();
        int four = numbers.tail().tail().head();
        System.out.println(two + " " + three + " " + four);

        primes();

        printAll(primes(LazyList.from(2)));

    }

    public static MyList<Integer> primes(MyList<Integer> numbers) {
        return new LazyList<>(
                numbers.head(),
                () -> primes(numbers.tail().filter(n -> n % numbers.head() != 0))
        );
    }


    private static void primes() {
        LazyList<Integer> numbers = LazyList.from(2);
        int two = primes(numbers).head();
        int three = primes(numbers).tail().head();
        int five = primes(numbers).tail().tail().head();
        System.out.println(two + " " + three + " " + five);
    }


    private static  <T> void printAll(MyList<T> list) {
        while (!list.isEmpty()) {
            System.out.println(list.head());
            list = list.tail();
        }
    }

}
