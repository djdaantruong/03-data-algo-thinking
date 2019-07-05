package vuongnguyen.handling_collisions;

public class LinearProbing extends HashFunction {

    public LinearProbing() {
        super();
    }

    public LinearProbing(int size) {
        super(size);
    }

    //When a collision occurs, the index is incremented and the item is put in the next available bucket in the array.
    public int getHash(String s, int attempt) {
        return (generic_hash(s, PRIME_1) + attempt)%size;
    }
}
