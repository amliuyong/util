package com.amliuyong.java;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;


@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2, jvmArgs = {"-Xms4G", "-Xmx4G"})
@Measurement(iterations = 2)
@Warmup(iterations = 3)
public class ParallelStreamBenchmark {

    private static final long N = 10_000_000L;

    @Benchmark
    public long sequentialSum() {
        return Stream.iterate(1L, i -> i + 1)
                .limit(N)
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long iterativeSum() {
        long result = 0;
        for (long i = 1L; i <= N; i++) {
            result += i;
        }
        return result;
    }

    @Benchmark
    public long parallelSum() {
        return Stream.iterate(1L, i -> i + 1)
                .limit(N)
                .parallel()
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long rangedSum() {
        return LongStream.rangeClosed(1, N)
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long parallelRangedSum() {
        return LongStream.rangeClosed(1, N)
                .parallel()
                .reduce(0L, Long::sum);
    }

    @Benchmark
    public long forkJoinSum() {
        long[] numbers = LongStream.rangeClosed(1, N).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);
    }


    @TearDown(Level.Invocation)
    public void tearDown() {
        System.gc();
    }

}

/*
// java -jar ./target/benchmarks.jar ParallelStreamBenchmark

Benchmark                                  Mode  Cnt   Score    Error  Units
ParallelStreamBenchmark.forkJoinSum        avgt    4  22.508 ±  9.390  ms/op
ParallelStreamBenchmark.iterativeSum       avgt    4   4.559 ±  0.284  ms/op
ParallelStreamBenchmark.parallelRangedSum  avgt    4   5.397 ± 27.649  ms/op
ParallelStreamBenchmark.parallelSum        avgt    4  96.241 ±  8.512  ms/op
ParallelStreamBenchmark.rangedSum          avgt    4   5.858 ±  0.041  ms/op
ParallelStreamBenchmark.sequentialSum      avgt    4  76.517 ±  8.625  ms/op
 */
