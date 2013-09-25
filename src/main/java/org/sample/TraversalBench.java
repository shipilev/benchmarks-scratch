package org.sample;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
public class TraversalBench {

    public static final int COUNT = 1000000;
    private Integer[] arr;

    @Setup
    public void prepare() {
        int base = 10000;
        arr = new Integer[COUNT];
        for (int c = base; c < base + COUNT; c++) {
            arr[c - base] = c;
        }
    }

    @GenerateMicroBenchmark
    @OutputTimeUnit(TimeUnit.SECONDS)
    public int test() {
        int s = 0;
        for (int i : arr) {
            s += i;
        }
        return s;
    }

}
