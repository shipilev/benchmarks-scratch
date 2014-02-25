package org.sample;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArrayFill {
    public static final int N = Integer.getInteger("N", 380);
    public static final Unsafe U;

    private static final int INT_BASE;
    private static final int INT_SCALE;
    private static final int REF_BASE;
    private static final int REF_SCALE;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            U = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        INT_BASE = U.arrayBaseOffset(int[].class);
        INT_SCALE = U.arrayIndexScale(int[].class);
        REF_BASE = U.arrayBaseOffset(int[][].class);
        REF_SCALE = U.arrayIndexScale(int[][].class);
    }

    public int[][] arr;
    private int[] t;

    @Setup
    public void setup() {
        arr = new int[N][N];
        t = new int[N];
        for (int i = 0; i < N; i++) {
            t[i] = i;
        }
    }

    @GenerateMicroBenchmark
    public int[][] simple() {
        int[][] g = arr;
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < N; j++) {
                g[i][j] = i + j;
            }
        }
        return arr;
    }

    @GenerateMicroBenchmark
    @Fork(jvmArgsAppend = "-XX:-UseCompressedOops") // we know our production runs in this mode!
    public int[][] simple_noBound() {
        int[][] g = arr;
        long off1 = REF_BASE;
        for(int i = 0; i < N; i++) {
            long giAddr = U.getLong(g, off1);
            long off2 = giAddr + INT_BASE;
            for(int j = 0; j < N; j++) {
                U.putInt(off2, i + j);
                off2 += INT_SCALE;
            }
            off1 += REF_SCALE;
        }
        return arr;
    }

    @GenerateMicroBenchmark
    public int[][] optimized() {
        int[][] g = arr;
        for(int i = 0; i < N; i++) {
            g[i][i] = 2 * i;
            for(int j = 0; j < i; j++) {
                g[j][i] = g[i][j] = i + j;
            }
        }
        return arr;
    }
}