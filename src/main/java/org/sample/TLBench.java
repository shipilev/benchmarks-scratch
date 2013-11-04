package org.sample;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class TLBench {

    public final Object INSTANCE = new Object();

    protected final ThreadLocal<Object> threadLocal = new ThreadLocal<Object>(){
        @Override
        protected Object initialValue() {
            return INSTANCE;
        }
    };

    @GenerateMicroBenchmark
    public void remove() {
        threadLocal.remove();
    }

    @GenerateMicroBenchmark
    public Object get() {
        return threadLocal.get();
    }

    @GenerateMicroBenchmark
    public void set() {
        threadLocal.set(INSTANCE);
    }

    @GenerateMicroBenchmark
    public Object remove_get() {
        threadLocal.remove();
        return threadLocal.get();
    }

    @GenerateMicroBenchmark
    public Object get_get() {
        threadLocal.get();
        return threadLocal.get();
    }

    @GenerateMicroBenchmark
    public void remove_set() {
        threadLocal.remove();
        threadLocal.set(INSTANCE);
    }

    @GenerateMicroBenchmark
    public void get_set() {
        threadLocal.get();
        threadLocal.set(INSTANCE);
    }

    @GenerateMicroBenchmark
    public void remove_remove() {
        threadLocal.remove();
        threadLocal.remove();
    }

    @GenerateMicroBenchmark
    public void get_remove() {
        threadLocal.get();
        threadLocal.remove();
    }

}