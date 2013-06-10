package org.sample;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.BenchmarkType;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class TestInlining {
    private static final int ARRAY_LENGTH = 100000;

    public static final class FieldArrayTest {
        public int value;

        void inc() {
            value++;
        }
    }

    public static FieldArrayTest[] testFieldDirect(int incCount) {
        FieldArrayTest[] a = new FieldArrayTest[ARRAY_LENGTH];

        for (int i = 0; i < ARRAY_LENGTH; i++) {
            a[i] = new FieldArrayTest();
        }

        while (incCount-- > 0) {
            for (int i = 0; i < ARRAY_LENGTH; i++) {
                a[i].value++;
            }
        }

        return a;
    }

    public static FieldArrayTest[] testFieldCall(int incCount) {
        FieldArrayTest[] a = new FieldArrayTest[ARRAY_LENGTH];

        for (int i = 0; i < ARRAY_LENGTH; i++) {
            a[i] = new FieldArrayTest();
        }

        while (incCount-- > 0) {
            for (int i = 0; i < ARRAY_LENGTH; i++) {
                a[i].inc();
            }
        }

        return a;
    }

    public static FieldArrayTest[] fill() {
        FieldArrayTest[] a = new FieldArrayTest[ARRAY_LENGTH];
        for (int i = 0; i < ARRAY_LENGTH; i++) {
            a[i] = new FieldArrayTest();
        }

        return a;
    }

    public static Object[] testFieldCall2(FieldArrayTest[] a, int incCount) {
        while (incCount-- > 0) {
            for (int i = 0; i < ARRAY_LENGTH; i++) {
                a[i].inc();
            }
        }

        return a;
    }

    @GenerateMicroBenchmark
    public Object[] testFieldDirect() {
        return testFieldDirect(1000);
    }

    @GenerateMicroBenchmark
    public Object[] incrementnFieldCall() {
        return testFieldCall(1000);
    }

    @GenerateMicroBenchmark
    public Object[] incrementnFieldCall2() {
        FieldArrayTest[] a = fill();
        return testFieldCall2(a, 1000);
    }
}
