package vuongnguyen.Dictionary;

//Strategy
public class DictionaryList {
    private Dictionary dictionary;

    public DictionaryList(){
        dictionary = Trie.getInstance();
    }

    public DictionaryList(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void setDictionary(Dictionary dictionary){
        this.dictionary = dictionary;
    }

    public boolean contains(String key){
        return dictionary.contains(key);
    }

    public void add(String key){
        dictionary.add(key);
    }
}
