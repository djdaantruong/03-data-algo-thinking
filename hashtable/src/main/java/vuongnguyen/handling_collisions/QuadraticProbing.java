package vuongnguyen.handling_collisions;

public class QuadraticProbing extends HashFunction {

    public QuadraticProbing() {
        super();
    }

    public QuadraticProbing(int size) {
        super(size);
    }

    //Similar to linear probing, but instead of putting the collided item in the next available bucket,
    //we try to put it in the buckets whose indexes follow the sequence: i, i + 1, i + 4, i + 9, i + 16, ..., where i is the original hash of the key.
    public int getHash(String s, int attempt) {
        return (generic_hash(s, PRIME_1) + attempt*attempt)%size;
    }
}
