package vuongnguyen.handling_collisions;

public class DoubleHashing extends HashFunction {

    public DoubleHashing() {
        super();
    }

    public DoubleHashing(int size) {
        super(size);
    }

    //We use a second hash function to choose a new index for the item.
    //Using a hash function gives us a new bucket, the index of which should be evenly distributed across all buckets.
    public int getHash(String s, int attempt) {
        int hash_a = generic_hash(s, PRIME_1);
        int hash_b = generic_hash(s, PRIME_2);
        return (hash_a + (attempt * (hash_b == 0 ? 1 : hash_b))) % size;
    }
}
