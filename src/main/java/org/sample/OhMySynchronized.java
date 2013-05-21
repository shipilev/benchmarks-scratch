package org.sample;

import org.openjdk.jmh.annotations.BenchmarkType;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class OhMySynchronized {

    @State(Scope.Benchmark)
    public static class MyState {

        private volatile int vv = 42;
        private int v = 42;
        private AtomicInteger av = new AtomicInteger(42);

        public synchronized int syncGet() {
            return v;
        }

        public int atomicGet() {
            return av.get();
        }

        public int volatileGet() {
            return vv;
        }

    }

    @GenerateMicroBenchmark(BenchmarkType.AverageTimePerOp)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int sync(MyState s) {
        return s.syncGet();
    }

    @GenerateMicroBenchmark(BenchmarkType.AverageTimePerOp)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int atomic(MyState s) {
        return s.atomicGet();
    }

    @GenerateMicroBenchmark(BenchmarkType.AverageTimePerOp)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public int vltile(MyState s) {
        return s.vv;
    }

}
