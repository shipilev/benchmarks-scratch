package org.sample.api;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.logic.BlackHole;
import org.openjdk.jmh.logic.results.Result;
import org.openjdk.jmh.logic.results.RunResult;
import org.openjdk.jmh.output.OutputFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.parameters.TimeValue;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Estimate the break-even threshold for parallel vs. sequential Stream traversal.
 *
 * This test is faulty for many reasons, use this only as demo.
 *   a) we measure the hot performance, meaning the backing thread pool is almost always busy-waiting for us
 *   b) warmup and measurement durations are nowhere sensible, but we want faster runs >:-)
 *   c) we "just" compare the averages, which makes the test fluctuate in the gray area of statistically
 *      insignificant differences
 */
@State(Scope.Benchmark)
public class ArrayListBench {

    /**
     * Number of elements in the source.
     */
    private static final int N = Integer.getInteger("benchmark.n", 1);

    /**
     * Relative "cost" of single operation on the element
     */
    private static final int Q = Integer.getInteger("benchmark.q", 1);

    private List<Integer> src;

    @Setup
    public void prepare() {
        src = new ArrayList<>();
        for (int c = 0; c < N; c++) {
            src.add(c);
        }
    }

    @GenerateMicroBenchmark
    public int testPar() {
        return src.parallelStream()
                .map((x) -> {
                    BlackHole.consumeCPU(Q);
                    return x * 2;
                })
                .reduce(Math::max)
                .get();
    }

    @GenerateMicroBenchmark
    public int testSeq() {
        return src.stream()
                .map((x) -> {
                    BlackHole.consumeCPU(Q);
                    return x * 2;
                })
                .reduce(Math::max)
                .get();
    }

    public static void main(String[] args) throws RunnerException {
        PrintWriter pw = new PrintWriter(System.out, true);

        for (int n = 1; n < 10000; n *= 10) {
            int qMin = 0;
            int qMax = 100000;
            while (Math.abs(qMin - qMax) > 10) {
                int q = (qMin + qMax) / 2;
                pw.printf(" trying: n=%d, q=%d", n, q);

                double pTime = runPar(n, q);
                pw.printf(", par=(%.2f ns, %.2f ns/op, %.2f ns/atom)", pTime, pTime / n, pTime / n / q);

                double sTime = runSeq(n, q);
                pw.printf(", seq=(%.2f ns, %.2f ns/op, %.2f ns/atom)", sTime, sTime / n, sTime / n / q);

                pw.printf(", speedup=%.2fx\n", sTime / pTime);

                // TODO: Do proper inference, instead of just comparing the averages
                if (pTime > sTime) {
                    qMin = q;
                } else {
                    qMax = q;
                }
            }

            int breakEven = (qMin + qMax) / 2;
            pw.printf("break-even point: n=%d, q=%d, n*q=%d\n", n, breakEven, n*breakEven);
        }
    }

    public static double runSeq(int n, int q) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(".*ArrayList.*testSeq")
                .jvmArgs("-Dbenchmark.n=" + n + " -Dbenchmark.q=" + q)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .warmupIterations(3)
                .warmupTime(TimeValue.milliseconds(100))
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(100))
                .outputFormat(OutputFormatType.Silent)
                .forks(5)
                .build();

        RunResult runResult = new Runner(opts).runSingle();
        Result result = runResult.getPrimaryResult();
        return result.getScore();
    }

    public static double runPar(int n, int q) throws RunnerException {
        Options opts = new OptionsBuilder()
                .include(".*ArrayList.*testPar")
                .jvmArgs("-Dbenchmark.n=" + n + " -Dbenchmark.q=" + q)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .warmupIterations(3)
                .warmupTime(TimeValue.milliseconds(100))
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(100))
                .outputFormat(OutputFormatType.Silent)
                .forks(5)
                .build();

        RunResult runResult = new Runner(opts).runSingle();
        Result result = runResult.getPrimaryResult();
        return result.getScore();
    }

}
