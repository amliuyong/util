package com.amliuyong.java;

import java.util.concurrent.RecursiveTask;

public class ForkJoinSumCalculator extends RecursiveTask<Long> {
    private long[] numbers;
    private int start;
    private int end;

    public ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }


    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    @Override
    protected Long compute() {
        if (end - start < 1000) {
            return computeSequential(numbers, start, end);
        }

        int midPos = start + (end - start) / 2;

        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, midPos);
        leftTask.fork();

        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, midPos, end);

        long rightSum = rightTask.compute();
        long leftSum = leftTask.join();

        return leftSum + rightSum;
    }

    private Long computeSequential(long[] numbers, int start, int end) {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }
}
