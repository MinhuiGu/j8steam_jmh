package com.ms.tech;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.HotspotRuntimeProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * i - number of benchmarked iterations, use 10 or more to get a good idea
 * r - how long to run each benchmark iteration
 * wi - number of warmup iterations
 * w - how long to run each warmup iteration (give ample room for warmup, how much will depend on the code you try and measure,
 * try and have it execute 100K times or so)
 *
 */
public class JMHStreaming {

    @org.openjdk.jmh.annotations.State(Scope.Benchmark)
    public static class State {
        private static final int N = 10000;
        final List<Transaction> transactions;

        public State() {
            transactions = new ArrayList<Transaction>(N);
            for (int i = 0; i < N; ++i)
                transactions.add(new Transaction("FromAccount" + String.valueOf(i),
                        "ToAccount" + String.valueOf(i), (double) i, Calendar.getInstance(), "Group" + i % 5));
        }
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void streamForEach(State state) {
        state.transactions.forEach(item -> {
            // do nothing
        });
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public void iterativeForEach(State state) {
        for (Transaction transaction : state.transactions) {
            // do nothing
        }
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Transaction streamMin(State state) {
        return state.transactions.stream().min(Transaction.amountComparator).get();
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Transaction iterativeMin(State state) {
        double min = Double.MAX_VALUE;
        Transaction t = null;
        for (Transaction transaction : state.transactions) {
            if (transaction.getAmount() < min) {
                t = transaction;
            }
        }
        return t;
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Double streamAvg(State state) {
        return state.transactions.stream().mapToDouble(Transaction::getAmount).average().getAsDouble();
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public double iterativeAvg(State state) {
        double sum = 0;
        for (Transaction transaction : state.transactions) {
            sum += transaction.getAmount();
        }
        return sum / state.transactions.size();
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public List<String> streamMapGetFromAccountNames(State state) {
        return state.transactions.stream().map(Transaction::getFromAccoundName).collect(Collectors.toList());
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public List<String> iterativeGetFromAccountNames(State state) {
        List<String> fromAccounts = new ArrayList<String>();
        for (Transaction transaction : state.transactions) {
            fromAccounts.add(transaction.getFromAccoundName());
        }
        return fromAccounts;
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public List<Transaction> streamFilterGetEven(State state) {
        return state.transactions.stream().filter(p -> p.getAmount() % 2 == 0).collect(Collectors.toList());
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public List<Transaction> iterativeGetEven(State state) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        for (Transaction transaction : state.transactions) {
            if (transaction.getAmount() % 2 == 0) {
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public List<Transaction> streamSortByName(State state) {
        return state.transactions.stream().sorted(Transaction.fromNameComparator).collect(Collectors.toList());
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public List<Transaction> iterativeSortByName(State state) {
        List<Transaction> replica = new ArrayList<Transaction>(state.transactions);
        replica.sort(Transaction.fromNameComparator);
        return replica;
    }


    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Map<String, List<Transaction>> streamGroupByAmount(State state) {
        return state.transactions.stream().collect(Collectors.groupingBy(Transaction::getGroup));
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Map<String, List<Transaction>> iterativeGroupByAmount(State state) {
        Map<String, List<Transaction>> groups = new HashMap<String, List<Transaction>>();
        for (Transaction transaction : state.transactions) {
            if (groups.containsKey(transaction.getGroup())) {
                groups.get(transaction.getGroup()).add(transaction);
            } else {
                groups.put(transaction.getGroup(), new ArrayList<Transaction>());
                groups.get(transaction.getGroup()).add(transaction);
            }
        }
        return groups;
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Double streamAggregates(State state) {
        //get sum of top 3 even amount of transactions in group2
        Double sum = state.transactions.stream()
                .filter(p -> p.getAmount() % 2 == 0)
                .filter(p -> p.getGroup().equals("Group2"))
                .sorted(Transaction.fromNameComparator)
                .limit(3).map(p -> p.getAmount()).reduce(Double::sum).orElse(0.0);
        return sum;
    }

    @Benchmark
    //@BenchmarkMode({Mode.AverageTime, Mode.Throughput, Mode.SingleShotTime})
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    public Double iterativeAggregates(State state) {
        //get sum of top 3 even amount of transactions in group2
        Double sum = 0.0;
        List<Transaction> list = new ArrayList<Transaction>();
        for (Transaction transaction : state.transactions) {
            if (transaction.getGroup().equals("Group2") && transaction.getAmount() % 2 == 0) {
                list.add(transaction);
            }
        }
        list.sort(Transaction.fromNameComparator);
        List<Transaction> top3 = list.subList(0, 3);
        for (Transaction transaction : top3) {
            sum += transaction.getAmount();
        }
        return sum;
    }

    /*
     * Run from IDE
     */
    public final static void main(String[] args) throws RunnerException {

        final Options opt = new OptionsBuilder()
                .jvmArgs("-XX:+UnlockCommercialFeatures")
                .include(JMHStreaming.class.getSimpleName())
                .warmupIterations(10)
                .measurementIterations(10)
                .mode(Mode.AverageTime)
                .verbosity(VerboseMode.EXTRA)
                .forks(1)
                //.addProfiler(StackProfiler.class)
                .addProfiler(HotspotRuntimeProfiler.class)
                //.addProfiler(GCProfiler.class)
                //.addProfiler(HotspotMemoryProfiler.class)
                .build();

        new Runner(opt).run();
    }
}
