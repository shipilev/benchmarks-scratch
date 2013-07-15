package org.sample;

import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.math.BigInteger;
import java.util.Arrays;

@State(Scope.Thread)
public class CopyOfVsClone {

    private BigInteger[] objs;

    @Setup
    public void setup() {
        BigInteger[] bs = new BigInteger[100];
        for (int c = 0; c < 100; c++) {
            bs[c] = BigInteger.valueOf(c * 100);
        }
        objs = bs;
    }

    @GenerateMicroBenchmark
    public BigInteger[] doClone() {
        return objs.clone();
    }

    @GenerateMicroBenchmark
    public BigInteger[] doCopyOf() {
        return Arrays.copyOf(objs, objs.length);
    }

}
