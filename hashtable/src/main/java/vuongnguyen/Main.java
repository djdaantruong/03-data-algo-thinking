package vuongnguyen;

import vuongnguyen.handling_collisions.DoubleHashing;
import vuongnguyen.hash_table.HashTable;

public class Main {
  public static void main(String[] args) {

      HashTable hashTable = new HashTable();
      hashTable.setHashFunction(new DoubleHashing());

      hashTable.insert("cat","moew");
      hashTable.insert("cat2","moew2");
      hashTable.insert("cat","moew_t");
      hashTable.insert("cat3","moew3");
      hashTable.insert("cat4","moew");
      hashTable.insert("cat5","moew");
      hashTable.insert("cat6","moew");
      hashTable.insert("cat7","moew");
      hashTable.delete("cat");
      hashTable.delete("cat2");
      hashTable.resize(20);

      System.out.println(hashTable.getBucketArr());
      System.out.println(hashTable.search("cat3"));
  }
}
