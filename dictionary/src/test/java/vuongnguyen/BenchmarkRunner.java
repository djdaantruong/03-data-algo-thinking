package vuongnguyen;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import vuongnguyen.DataLoader.DataReader;
import vuongnguyen.Dictionary.BloomFilter;
import vuongnguyen.Dictionary.Trie;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class BenchmarkRunner {

    @State(Scope.Benchmark)
    public static class MyState{
        Trie trie;
        BloomFilter bloomFilter;
        String key;

        @Setup(Level.Invocation)
        public void setUp(){
            trie=Trie.getInstance();
            bloomFilter=BloomFilter.getInstance();
            key = getRandomKey();
        }

        private String getRandomKey(){
            String res="";
            Random random = new Random();
            int length = random.nextInt(5 - 1 + 1) + 1;
            for(int i = 0; i < length;i++){
                char c = (char)(random.nextInt('z' - 'a' + 1) + 'a');
                res+=c;
            }
            return res;
        }
    }

    @Setup
    public void initData(){
        try {
            DataReader.read(System.getProperty("user.dir") + "/blogs");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    public static boolean benchMarkTrie(MyState state) {
        return state.trie.contains(state.key);
    }

    @Benchmark
    public static boolean benchMarkBloomFilter(MyState state) {
        return state.bloomFilter.contains(state.key);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(".*" + BenchmarkRunner.class.getSimpleName() + ".*")
                .build();
        new Runner(opt).run();
    }
}
