package vuongnguyen.Dictionary;

import java.util.BitSet;
import java.util.Random;
import java.util.Iterator;

public class BloomFilter implements Dictionary{
    private BitSet hashes;
    private RandomInRange prng;
    private int k; // Số lượng hàm băm
    private static final double LN2 = 0.6931471805599453; // ln(2)
    private static final int MAX_ELEMENTS = 28000000;

    private BloomFilter(){};

    //Tạo một bloom filter
    // n : số lượng phần tử ước tính
    // m : Kích thước dự trù của BitSet
    private BloomFilter(int n, int m) {
        k = (int) Math.round(LN2 * m / n);
        if (k <= 0) k = 1;
        this.hashes = new BitSet(m);
        this.prng = new RandomInRange(m, k);
    }

    private BloomFilter(int n) {
        this(n, 1024*1024*8);
    }

    private static class SingletonHelper{
        private static final BloomFilter INSTANCE = new BloomFilter(MAX_ELEMENTS);
    }

    public static BloomFilter getInstance(){
        return BloomFilter.SingletonHelper.INSTANCE;
    }

    //Đánh dấu phần tử vào BitSet
    public synchronized void add(String key) {
        prng.init(key);
        for (RandomInRange r : prng) hashes.set(r.value);
    }

    // Trả về true nếu phần tử nằm trong BitSet
    // Trả về false với xác suất 1-e ^ (- ln (2) ² * m / n)
    // nếu phần tử không có trong BitSet
    public boolean contains(String key) {
        prng.init(key);
        for (RandomInRange r : prng)
            if (!hashes.get(r.value))
                return false;
        return true;
    }

    //Class thực hiện k hàm băm
    private class RandomInRange
            implements Iterable<RandomInRange>, Iterator<RandomInRange> {

        private Random prng;
        private int max; // Kích thước của BitSet
        private int count; // Số lượng hàm băm
        private int i = 0; // Biến đếm i
        public int value; // The current value (index in BitSet)

        RandomInRange(int maximum, int k) {
            max = maximum;
            count = k;
            prng = new Random();
        }
        public void init(Object o) {
            prng.setSeed(o.hashCode());
        }
        public Iterator<RandomInRange> iterator() {
            i = 0;
            return this;
        }
        public RandomInRange next() {
            i++;
            value = prng.nextInt() % max;
            if (value<0) value = -value;
            return this;
        }
        public boolean hasNext() {
            return i < count;
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
