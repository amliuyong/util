package com.amliuyong.java.future;


import java.util.Random;
import java.util.concurrent.*;
import java.util.function.IntConsumer;

public class ThreadExample {
    private static class Result {
        private int left;
        private int right;
    }

    private static int g(int x) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(100);
    }

    private static int f(int x) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Random().nextInt(100);
    }

    private static void g(int x, IntConsumer cb) {
        cb.accept(x * 10);
    }

    private static void f(int x, IntConsumer cb) {

        cb.accept(x * 5);
    }

    private static void f(int x, Flow.Subscriber<Integer> s) {
        s.onNext(x * 5);
    }


    public static void doThread() throws InterruptedException {
        int x = 1337;
        Result result = new Result();
        Thread t1 = new Thread(() -> {
            result.left = f(x);
        });
        Thread t2 = new Thread(() -> {
            result.right = g(x);
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(result.left + result.right);
    }

    public static void doExecutorService() throws ExecutionException, InterruptedException {
        int x = 1337;
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> y = executorService.submit(() -> f(x));
        Future<Integer> z = executorService.submit(() -> g(x));
        System.out.println(y.get() + z.get());
        executorService.shutdown();
    }


    public static void doCallBack() {
        int x = 1337;
        Result result = new Result();
        f(x, (int y) -> {
            result.left = y;
            System.out.println((result.left + result.right));
        });

        g(x, (int z) -> {
            result.right = z;
            System.out.println((result.left + result.right));
        });
    }


    public static void doCallBack2() {
        int x = 1337;
        Result result = new Result();
        f(x, (int y) -> {
            result.left = y;

            g(x, (int z) -> {
                result.right = z;
                System.out.println((result.left + result.right));
            });
        });
    }


    public static void scheduledExecutorServiceExample() {

            /*
            // do not do this, use ScheduledExecutorService
            work1();
            Thread.sleep(10000);
            work2();
             */

        ScheduledExecutorService scheduledExecutorService
                = Executors.newScheduledThreadPool(1);

        work1();

        scheduledExecutorService.schedule(
                ThreadExample::work2, 10,
                TimeUnit.SECONDS);

        scheduledExecutorService.shutdown();
    }

    public static void work1() {
        System.out.println("Hello from Work1!");
    }

    public static void work2() {
        System.out.println("Hello from Work2!");
    }

    public static void doCFComplete() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;
        CompletableFuture<Integer> a = new CompletableFuture<>();
        executorService.submit(() -> a.complete(f(x)));
        int b = g(x);
        System.out.println(a.get() + b);
        executorService.shutdown();
    }

    public static void doCFCombine() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;
        CompletableFuture<Integer> a = new CompletableFuture<>();
        CompletableFuture<Integer> b = new CompletableFuture<>();

        CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);

        executorService.submit(() -> a.complete(f(x)));
        executorService.submit(() -> b.complete(g(x)));

        System.out.println(c.get());

        executorService.shutdown();

    }


}