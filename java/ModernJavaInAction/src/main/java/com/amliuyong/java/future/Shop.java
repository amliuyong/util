package com.amliuyong.java.future;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


public class Shop {
    private String name;
    private Random random = new Random();

    public Shop(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getPrice_1(String product) {
        return calculatePrice(product);
    }

    public String getPrice(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[
                random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

    public Future<Double> getPriceAsync_1(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                futurePrice.complete(price);
            } catch (Exception e) {
                futurePrice.completeExceptionally(e);
            }
        }).start();
        return futurePrice;
    }


    public Future<Double> getPriceAsync(String product) {
        return CompletableFuture.supplyAsync(
                () -> calculatePrice(product), Util.getThreadPool()
        );
    }


    private double calculatePrice(String product) {
        //Util.delay();
        Util.randomDelay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }


}


class Discount {
    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
        //Util.delay();
        Util.randomDelay();
        return Util.format(price * (100 - code.percentage) / 100);
    }
}


class Quote {
    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public Quote(String shopName, double price, Discount.Code code) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = code;
    }

    public static Quote parse(String s) {
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code discountCode = Discount.Code.valueOf(split[2]);
        return new Quote(shopName, price, discountCode);
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDiscountCode() {
        return discountCode;
    }
}


class Util {
    private static final Random random = new Random();

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Shop> getShops() {
        return List.of(new Shop("Shop1"),
                new Shop("Shop2"),
                new Shop("Shop3"),
                new Shop("Shop4"),
                new Shop("Shop5"),
                new Shop("Shop6")

        );
    }

    public static Executor getThreadPool() {
        return Executors.newFixedThreadPool(Math.min(getShops().size(), 100),
                (Runnable r) -> {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
        );
    }

    public static double format(double v) {
        return ((int) (v * 100)) / 100;
    }
}
