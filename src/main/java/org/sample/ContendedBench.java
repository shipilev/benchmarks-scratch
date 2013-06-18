package org.sample;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@State(Scope.Group)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ContendedBench {

    // Run with -XX:-RestrictContended
    // Padding size is configurable with -XX:ContendedPaddingWidth=#

    @sun.misc.Contended
    private volatile int a1;

    @sun.misc.Contended
    private volatile int a2;

    @Group("coll")
    @GenerateMicroBenchmark
    public void a1() {
        a1++;
    }

    @Group("coll")
    @GenerateMicroBenchmark
    public void a2() {
        a2++;
    }

}
