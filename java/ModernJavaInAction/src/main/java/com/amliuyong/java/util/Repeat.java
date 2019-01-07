package com.amliuyong.java.util;

import java.util.function.Function;

public class Repeat {
    public static <A> Function<A, A> repeat(int n, Function<A, A> f) {
        if (n == 0) return x -> x;
        return f.compose(repeat(n - 1, f));
    }

    public static void main(String... args) {
      int result =  repeat(3, (Integer x )-> 2 * x).apply(10);
      System.out.println(result);
    }
}
