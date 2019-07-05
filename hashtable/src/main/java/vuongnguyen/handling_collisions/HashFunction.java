package vuongnguyen.handling_collisions;

public abstract class HashFunction {
    protected static final int PRIME_1 = 151;
    protected static final int PRIME_2 = 163;
    protected int size;

    public HashFunction(){
        this.size = 53;
    }

    public HashFunction(int num){
        this.size=num;
    }

    public void setSize(int size) {
        this.size = size;
    }

    //Convert the string to a large integer
    //Reduce the size of the integer to a fixed range by taking its remainder mod m
    protected int generic_hash(String s, int prime){
        long hash = 0;
        int len_s = s.length();

        for(int i=0;i<len_s;i++){
            hash+=(long)Math.pow(prime, len_s - (i+1))*s.charAt(i);
            hash=hash % size;
        }
        return (int)hash;
    }

    public abstract int getHash(String s, int attempt);
}
