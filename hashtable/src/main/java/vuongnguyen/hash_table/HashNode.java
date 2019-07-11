package vuongnguyen.hash_table;

public class HashNode{
    private String key;
    private String value;

    public HashNode(String k, String v){
        key = k;
        value = v;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key+":"+value;
    }

    @Override
    public boolean equals(Object obj) {
        return key.equals(((HashNode)obj).getKey()) && value.equals(((HashNode)obj).getValue());
    }
}
