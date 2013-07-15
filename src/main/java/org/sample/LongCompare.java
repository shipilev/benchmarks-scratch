package org.sample;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State
public class LongCompare {

    long L = -1L;
    long Z = 0L;
    long G = 1L;

    @GenerateMicroBenchmark
    public int LZ_signed() {
        return Long.compare(L, Z);
    }

    @GenerateMicroBenchmark
    public int ZG_signed() {
        return Long.compare(Z, G);
    }

    @GenerateMicroBenchmark
    public int GL_signed() {
        return Long.compare(G, L);
    }

    @GenerateMicroBenchmark
    public int LZG_signed() {
        return Long.compare(L, Z) + Long.compare(Z, G);
    }

    @GenerateMicroBenchmark
    public int LZ_unsigned() {
        return Long.compareUnsigned(L, Z);
    }

    @GenerateMicroBenchmark
    public int ZG_unsigned() {
        return Long.compareUnsigned(Z, G);
    }

    @GenerateMicroBenchmark
    public int GL_unsigned() {
        return Long.compareUnsigned(G, L);
    }

    @GenerateMicroBenchmark
    public int LZG_unsigned() {
        return Long.compareUnsigned(L, Z) + Long.compareUnsigned(Z, G);
    }

}
