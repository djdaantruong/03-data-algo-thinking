package com.zalopay;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class BenchmarkRunner {

    @State(Scope.Benchmark)
    public static class MyState{
        LocalDateTime fromDateTime;
        LocalDateTime toDateTime;
        SlaServiceImpl slaService;

        @Setup(Level.Invocation)
        public void setUp(){
            fromDateTime = getRandomDateTime();
            toDateTime = getRandomDateTime();

            if(fromDateTime.isAfter(toDateTime)){
                LocalDateTime temp = fromDateTime;
                fromDateTime = toDateTime;
                toDateTime = temp;
            }
            slaService = new SlaServiceImpl();
        }

        private LocalDateTime getRandomDateTime(){
            int maxYear = 2019;
            int minYear = 1998;

            Random random = new Random();
            int year = random.nextInt((maxYear - minYear) + 1) + minYear;
            int month = random.nextInt(12 - 1 + 1) + 1;
            int dayOfMonth = random.nextInt(28 - 1 + 1) + 1;
            int hour = random.nextInt(23 - 0 + 1) + 0;
            int minute = random.nextInt(59 - 0 + 1) + 0;
            int second = random.nextInt(59 - 0 + 1) + 0;
            return LocalDateTime.of(year, month, dayOfMonth, hour, minute, second);
        }
    }

    @Benchmark
    public static Duration benchMarkMethod(MyState state) {
        return state.slaService.calculate(state.fromDateTime,state.toDateTime);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(".*" + BenchmarkRunner.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }
}
