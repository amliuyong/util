package com.amliuyong.java.future;


import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class AsyncApp {

    public static List<String> findPrices_1(String product) {
        return Util.getShops().stream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }

    public static List<String> findPrices_2(String product) {
        return Util.getShops().parallelStream()
                .map(shop -> String.format("%s price is %.2f",
                        shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }

    public static List<String> findPrices_3(String product) {
        List<CompletableFuture<String>> priceFutures =
                Util.getShops().stream()
                        .map(shop -> CompletableFuture.supplyAsync(
                                () -> String.format("%s price is %.2f",
                                        shop.getName(), shop.getPrice(product))))
                        .collect(toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(toList());

    }


    public static List<String> findPrices_4(String product) {
        return Util.getShops().stream()
                .map(shop -> shop.getPrice(product))
                .map(Quote::parse)
                .map(Discount::applyDiscount)
                .collect(toList());
    }

    public static List<String> findPrices(String product) {
        Executor executor = Util.getThreadPool();

        Function<Shop, CompletableFuture<String>> getPriceAsync =
                shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product), executor);

        Function<Quote, CompletableFuture<String>> applyDiscountAsync =
                quote -> CompletableFuture.supplyAsync(
                        () -> Discount.applyDiscount(quote), executor);

        /*

        Future<Double> futurePriceInUSD =
                CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product))
                        .thenCombine(
                                CompletableFuture.supplyAsync(
                                        () -> exchangeService.getRate(Money.EUR, Money.USD))
                                        .completeOnTimeout(DEFAULT_RATE, 1, TimeUnit.SECONDS),

                                (price, rate) -> price * rate
                        )
                        .orTimeout(3, TimeUnit.SECONDS);

        */

        List<CompletableFuture<String>> priceFutures =
                Util.getShops().stream()
                        .map(getPriceAsync)
                        .map(future -> future.thenApply(Quote::parse))
                        .map(future -> future.thenCompose(applyDiscountAsync))
                        //.map(future -> future.orTimeout(3, TimeUnit.SECONDS))
                        .collect(toList());

        return priceFutures.stream().map(CompletableFuture::join)
                .collect(toList());
    }


    public static Stream<CompletableFuture<String>> findPricesStream(String product) {
        Executor executor = Util.getThreadPool();
        //  thenApply  --> map
        //  thenCompose --> flatMap
        return Util.getShops().stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote), executor)));
    }


    public static void showPriceRealTime() {
        CompletableFuture<Void>[] futures = findPricesStream("myPhone27S")
                .map(future -> future.thenAccept(System.out::println))
                .toArray((IntFunction<CompletableFuture<Void>[]>) CompletableFuture[]::new);
        CompletableFuture.allOf(futures).join();

    }


    public static void showPriceRealTime_2() {

        long start = System.nanoTime();
        CompletableFuture[] futures = findPricesStream("myPhone27S")
                .map(f -> f.thenAccept(
                        s -> System.out.println(s + " (done in " +
                                ((System.nanoTime() - start) / 1_000_000) + " msecs)")))
                .toArray(size -> new CompletableFuture[size]);

        CompletableFuture.allOf(futures).join();

        System.out.println("All shops have now responded in "
                + ((System.nanoTime() - start) / 1_000_000) + " msecs");

    }

    public static void main(String... args) {
        /*
        long start = System.nanoTime();
        System.out.println(findPrices("myPhone27S"));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs");
        */
        //showPriceRealTime();
        showPriceRealTime_2();
    }

}
