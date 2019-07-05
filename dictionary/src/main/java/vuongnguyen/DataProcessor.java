package vuongnguyen;

import java.util.*;
import java.util.regex.*;

public class DataProcessor {

    public static Set<String> splitWord(String line){
        String[] words = line.split("[\\s]+");
        return new HashSet<String>(Arrays.asList(words));
    }

    public static void addDataIntoApp(Set<String> listWord){

        for(String word : listWord){
            Trie.getInstance().add(word);
            BloomFilter.getInstance().add(word);
        }
    }
}


