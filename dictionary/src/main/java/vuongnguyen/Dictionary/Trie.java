package vuongnguyen.Dictionary;

import java.util.*;

public class Trie implements Dictionary {
    
    class TrieNode
    {
        Map<Character, TrieNode> children = new HashMap<Character, TrieNode>();

        // isEndOfWord là true nếu node cuối của từ
        boolean isEndOfWord;

        // isVisit là true nếu đã xét qua node đó
        boolean isVisit;

        TrieNode(){
            isEndOfWord = false;
            isVisit = false;
        }
    };

    private TrieNode root;

    public TrieNode getRoot() {
        return root;
    }

    private Trie(){
        root = new TrieNode();
    };

    private static class SingletonHelper{
        private static final Trie INSTANCE = new Trie();
    }

    public static Trie getInstance(){
        return SingletonHelper.INSTANCE;
    }

    // Trả về true nếu key có trong trie
    public boolean contains(String key) {
        int i;
        int length = key.length();
        char c;
        Trie.TrieNode pCrawl = root;

        for(i = 0; i < length; i++){
            c = key.charAt(i);

            if (pCrawl.children.containsKey(c) == false)
                return false;

            pCrawl = pCrawl.children.get(c);
        }

        return (pCrawl != null && pCrawl.isEndOfWord);
    }

    //Duyệt từng ký tự của từ
    //Nếu chưa có thì khởi tạo node mới
    //Nếu đã có thì mark với node chứa ký tự đó
    public synchronized void add(String key) {
        int i;
        int length = key.length();
        char c;

        TrieNode pCrawl = root;

        for (i = 0; i < length; i++)
        {
            c = key.charAt(i);
            if (pCrawl.children.containsKey(c) == false)
                pCrawl.children.put(c, new TrieNode());

            pCrawl = pCrawl.children.get(c);
        }

        pCrawl.isEndOfWord = true;
    }

    //Dựa vào key thực hiện gợi ý
    //Duyệt từng ký tự của từ
    //Nếu chưa có thì trả về NULL
    //Nếu đã có thì mark với node chứa ký tự đó, tiếp tục duyệt cho hết key
    //Dùng DFS để duyệt node tiếp , để tìm từ gợi ý
    public Set<String> query(String key, int maxNum)
    {
        Set<String> result = new LinkedHashSet<>();
        int i;
        int length = key.length();
        char c;
        TrieNode pCrawl = root;

        for (i = 0; i < length; i++)
        {
            c = key.charAt(i);

            if (pCrawl.children.containsKey(c) == false)
                return result;

            pCrawl = pCrawl.children.get(c);
        }
        DFS(pCrawl, key, result, maxNum);
        return result;
    }

    private void DFS(TrieNode pCrawl, String key, Set<String> result, int maxNum){
        pCrawl.isVisit = true;
        if(pCrawl.isEndOfWord == true){
            result.add(key);
            if(result.size() == maxNum){
                return;
            }
        }

        for(Character c : pCrawl.children.keySet())
        {
            if(pCrawl.children.get(c).isVisit == false){
                key+=c;
                DFS(pCrawl.children.get(c), key, result, maxNum);
                if(result.size() == maxNum){
                    return;
                }
                key = key.substring(0, key.length() - 1);
            }
        }
        pCrawl.isVisit = false;
    }
}

