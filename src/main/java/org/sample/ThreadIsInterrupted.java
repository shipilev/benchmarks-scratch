package org.sample;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ThreadIsInterrupted {

    private Thread thread;

    @Setup
    public void setup() {
        thread = new Thread () {
            public void run() {
                boolean isInt = false;
                while (!isInt) {
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException ie) {
                        isInt = true;
                    }
                }
            }
        };
        thread.start();
    }

    @TearDown
    public void tearDown() throws InterruptedException {
        thread.interrupt();
        thread.join();
    }

    @GenerateMicroBenchmark
    public boolean enabled() {
        return thread.isInterrupted();
    }

    @GenerateMicroBenchmark
    @Fork(jvmArgsAppend = {"-XX:+UnlockDiagnosticVMOptions", "-XX:DisableIntrinsic=_isInterrupted"})
    public boolean disabled() {
        return thread.isInterrupted();
    }

}
