package org.sample;

import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.annotations.Mode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(5)
@BenchmarkMode({Mode.SampleTime, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class StringToUTF8 {

    @Param({"1", "10", "100", "1000", "10000", "100000", "1000000"})
    private int length;

    private String aString;

    @Setup(Level.Iteration)
    public void generateRandomString() throws UnsupportedEncodingException {
        final char[] chars = new char[]{
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                'ä', 'ö', 'ü', 'Ä', 'Ö', 'Ü', 'ß', '€', 'Â', 'à',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
        };

        Random r = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(chars[r.nextInt(chars.length)]);
        }
        aString = builder.toString();
    }

    private static final Charset UTF_8_CHARSET = Charset.forName("UTF-8");

    @GenerateMicroBenchmark
    public byte[] getBytesWithString() throws UnsupportedEncodingException {
        return aString.getBytes("UTF-8");
    }

    @GenerateMicroBenchmark
    public byte[] getBytesWithCharset() throws UnsupportedEncodingException {
        return aString.getBytes(UTF_8_CHARSET);
    }

    @GenerateMicroBenchmark
    public byte[] getBytesViaWriter() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(bos, "UTF-8");
        osw.append(aString);
        return bos.toByteArray();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*")
                .forks(1)
                .build();

        System.out.println(opt);
        new Runner(opt).run();
    }
}