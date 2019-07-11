package vuongnguyen.hash_table;

import vuongnguyen.handling_prime.Prime;
import vuongnguyen.handling_collisions.*;

import java.util.ArrayList;

public class HashTable {
    private int numBuckets; //Size of Array
    private int count; //Count HashNode
    private ArrayList<HashNode> bucketArray; //Array of HashNode
    private int base_size;

    private HashFunction hashFunction;

    private static final int INITIAL_BASE_SIZE = 5;

    private static final HashNode DELETED_ITEM = new HashNode(null, null);

    public HashTable(){
        numBuckets = 7;

        hashFunction=new DoubleHashing(numBuckets);

        base_size = INITIAL_BASE_SIZE;
        count = 0;
        bucketArray = new ArrayList<HashNode>();
        for(int i=1;i<=numBuckets;i++){
            bucketArray.add(null);
        }
    }

    public void setHashFunction(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
        this.hashFunction.setSize(numBuckets);
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    public int getNumBuckets() {
        return numBuckets;
    }

    //To insert a new key-value pair, we iterate through indexes until we find an empty bucket.
    //We then insert the item into that bucket and increment the hash table's count attribute, to indicate a new item has been added.
    //Support updating a key's value.
    //If we insert two items with the same key, the keys will collide, and the second item will be inserted into the next available bucket.
    public void insert(String key, String value){
        int load=count*100/numBuckets;
        if(load>70){
            resize_up();
        }
        HashNode item = new HashNode(key, value);
        int index = hashFunction.getHash(item.getKey(), 0);
        HashNode cur_item = bucketArray.get(index);
        int i = 1;
        while (cur_item != null){
            if(cur_item != DELETED_ITEM){
                if(cur_item.getKey().equals(key)){
                    bucketArray.set(index, item);
                    return;
                }
            }
            index = hashFunction.getHash(item.getKey(), i);
            cur_item = bucketArray.get(index);
            i++;
        }
        bucketArray.set(index, item);
        count++;
    }

    //Searching is similar to inserting, but at each iteration of the while loop, we check whether the item's key matches the key we're searching for.
    //If it does, we return the item's value. If the while loop hits a NULL bucket, we return NULL, to indicate that no value was found.
    //When searching, we ignore and 'jump over' deleted nodes.
    public String search(String key){
        int index = hashFunction.getHash(key, 0);
        HashNode item = bucketArray.get(index);
        int i = 1;
        while (item != null){
            if(item != DELETED_ITEM){
                if(item.getKey().equals(key)){
                    return item.getValue();
                }
            }
            index=hashFunction.getHash(key, i);
            item = bucketArray.get(index);
            i++;
        }
        return null;
    }

    //We mark an item as deleted by replacing it with a pointer to a global sentinel item which represents that a bucket contains a deleted item.
    //After deleting, we decrement the hash table's count attribute.
    public void delete(String key){
        int load = count*100/numBuckets;
        if(load<10){
            resize_down();
        }
        int index=hashFunction.getHash(key, 0);
        HashNode item = bucketArray.get(index);
        int i = 1;
        while (item != null){
            if(item != DELETED_ITEM){
                if(item.getKey().equals(key)){
                    bucketArray.set(index, DELETED_ITEM);
                    count--;
                    return;
                }
            }
            index=hashFunction.getHash(key, i);
            item=bucketArray.get(index);
            i++;
        }
    }

    //To resize, we create a new hash table roughly half or twice as big as the current, and insert all non-deleted items into it.
    public void resize(int size){
        if(size<INITIAL_BASE_SIZE){
            return;
        }

        int oldNumBuckets=numBuckets;
        ArrayList<HashNode> oldBucketArray=(ArrayList<HashNode>)bucketArray.clone();
        base_size = size;
        numBuckets= Prime.next_prime(base_size);
        hashFunction.setSize(numBuckets);
        count=0;
        bucketArray=new ArrayList<HashNode>();
        for(int i=0;i<numBuckets;i++){
            bucketArray.add(null);
        }
        for(int i=0;i<oldNumBuckets;i++){
            HashNode item = oldBucketArray.get(i);
            if(item != null && item != DELETED_ITEM){
                insert(item.getKey(),item.getValue());
            }
        }
    }

    public void resize_up(){
        int new_size=base_size*2;
        resize(new_size);
    }

    public void resize_down(){
        int new_size=base_size/2;
        resize(new_size);
    }

    public String getBucketArr(){
        return bucketArray.toString();
    }
}
