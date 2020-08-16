import java.util.*;
// Keys will always be Strings in this implementation
// The hash table will store values, their keys, and a pointer to its position in the LinkedList
public class PHPArray<Value> implements Iterable<Value>
{
    // Class variables
    private static final int INIT_CAPACITY = 10;
    private int M;           // size of linear probing table
    private int N;            // number of key-value pairs in the symbol table

    public Node<Value>[] pairs;
    public ArrayList<Value> sortableValues;
    public ArrayList<String> keys;
    public Node<Value> root;
    public int pointer;

    public PHPArray()
    {
        this(INIT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public PHPArray(int capacity)
    {
        M = capacity;
        pairs = (Node<Value>[])new Node<?>[M];
        sortableValues = new ArrayList<Value>(M);
        keys = new ArrayList<String>(M);
        pointer = -1;
    }

    public int length()
    {
        return M;
    }

    // hash function for keys - returns value between 0 and M-1
    // Uses multiplicative constant 31^n
    public int hash(Object key) {
        String realKey = key.toString();
        return (realKey.hashCode() & 0x7fffffff) % M;
    }

    // insert the key-value pair into the symbol table
    public void put(Object key, Value val) {
        String realKey = key.toString();
        //if (val == null) delete(key);

        // double table size if 50% full
        if (sortableValues.size() >= M/2) resize(2*M);

        int i;
        for (i = hash(realKey); pairs[i] != null; i = (i + 1) % M) {
           if (pairs[i].getKey().equals(realKey)) { pairs[i].setValue(val); return; }
        }
        pairs[i] = new Node<Value>(realKey, val);
        linkedAdd(realKey, val);    // add to the linked list paired with this hash table
        N++;
    }

    // return the value associated with the given key, null if no such value
    public Value get(Object key) {
        String realKey = key.toString();

        for (int i = hash(realKey); pairs[i] != null; i = (i + 1) % M)
            if (pairs[i].getKey().equals(realKey))
                return pairs[i].getValue();
        return null;
    }

    // delete the key (and associated value) from the symbol table
    public void unset(Object key) {
        String realKey = key.toString();
        if (!contains(realKey)) return;

        // find position i of key
        int i = hash(realKey);
        while (!realKey.equals(pairs[i].getKey())) {
            i = (i + 1) % M;
        }

        // delete key and associated value
        pairs[i] = null;
        linkedDelete(realKey);

        // rehash all keys in same cluster
        i = (i + 1) % M;
        while (pairs[i] != null) {
            // delete keys[i] an vals[i] and reinsert
            String  keyToRehash = pairs[i].getKey();
            Value valToRehash = pairs[i].getValue();
            pairs[i] = null;
            N--;
            put(keyToRehash, valToRehash);
            i = (i + 1) % M;
        }

        N--;

        //halves size of array if it's 12.5% full or less
        if (sortableValues.size() <= M/8) resize(M/2);

        //assert check();
    }

    // NECESSARY METHODS //
    @SuppressWarnings("unchecked")
    public void sort()
    {
        Collections.sort(sortableValues, new Comparator<Value>() {
            @Override
            public int compare(Object o1, Object o2)
            {
                return ((Comparable<? super Value>)o1).compareTo((Value)o2);
            }
        });

        //reinstantiate the hash table and linkedlist, replacing old keys with their new ordering
        ArrayList<Value> newValues = new ArrayList<Value>(M);
        ArrayList<String> newKeys = new ArrayList<String>(M);
        String newKey = "";
        int cap = sortableValues.size();
        int i = 0;
        while (cap > i)
        {
            newKey = "" + i;
            put(newKey, sortableValues.get(i));
            newValues.add(sortableValues.get(i));
            newKeys.add(newKey);
            unset(keys.get(i));
            i++;
        }
        sortableValues = newValues;
        keys = newKeys;
    }

    @SuppressWarnings("unchecked")
    public void asort()
    {
        Collections.sort(sortableValues, new Comparator<Value>() {
            @Override
            public int compare(Object o1, Object o2)
            {
                return ((Comparable<? super Value>)o1).compareTo((Value)o2);
            }
        });

        ArrayList<Value> newValues = new ArrayList<Value>(M);
        ArrayList<String> newKeys = new ArrayList<String>(M);
        int cap = sortableValues.size();
        int i = 0;
        linkedClear();
        while (cap > i)
        {
            System.out.println(keys.get(i));
            put(keys.get(i), sortableValues.get(i));
            newValues.add(sortableValues.get(i));
            newKeys.add(keys.get(i));
            i++;
        }
        sortableValues = newValues;
        keys = newKeys;
    }

    public Pair<Value> each()
    {
        while (pointer < (M-1))
        {
            pointer++;
            if (pairs[pointer] != null) return pairs[pointer].pair;
        }
        return null;
    }

    public void reset()
    {
        pointer = -1;
    }

    public PHPArray<String> array_flip()
    {
        PHPArray<String> flip = new PHPArray<String>(M);
        int i = 0;
        while (i < M)
        {
            if (pairs[i] != null)
                flip.put(this.pairs[i].getValue().toString(), this.pairs[i].getKey());
            i++;
        }
        return flip;
    }

    public void showTable()
    {
        for (int i=0; i<M; i++)
        {
            if (pairs[i] == null) { System.out.println(i + ": null"); }
            else { System.out.println(i + ": Key = " + pairs[i].getKey() + "; Value = " + pairs[i].getValue()); }
        }
    }

    // HELPER METHODS //
    public boolean contains(String key) {
        return get(key) != null;
    }

    // resize the hash table to the given capacity by re-hashing all of the keys
    private void resize(int capacity) {
        PHPArray<Value> temp = new PHPArray<Value>(capacity);
        for (int i = 0; i < M; i++) {
            if (pairs[i] != null) {
                temp.put(pairs[i].getKey(), pairs[i].getValue());
            }
        }
        this.pairs = temp.pairs;
        M = capacity;
    }

    public Iterator<Value> iterator()
    {
        return new PHPIterator();
    }

    //        Linked List           //
    public void linkedAdd(String k, Value val)
    {
        Node<Value> n = null;
        if (root != null) { n = root; }
        root = new Node<Value>(k, val);
        root.next = n;
        sortableValues.add(val);
        keys.add(k);
    }

    public Value linkedGet(String k)
    {
        if (!contains(k)) { return null; }
        Node<Value> n = root;
        while (!n.getKey().equals(k))
        {
            n = n.next;
        }
        return n.getValue();
    }

    public boolean linkedDelete(String k)
    {
        if (!contains(k)) { return false; }
        Node<Value> n = root;
        while (n.next != null && !n.next.getKey().equals(k))
        {
            if (n.next.next == null)
                n.next = null;
            else
                n.next = n.next.next;
        }
        sortableValues.remove(get(k));
        keys.remove(k);
        System.out.println("remove? " + sortableValues.size());
        return true;
    }

    public void linkedClear()
    {
        root = null;
    }

    public ArrayList<String> keys()
    {
        ArrayList<String> keys = new ArrayList<String>();
        Node<Value> n = root.next;
        while (n.next != null)
        {
             keys.add(0, n.getKey());
             n = n.next;
        }
        return keys;
    }

    public ArrayList<Value> values()
    {
        ArrayList<Value> values = new ArrayList<Value>();
        Node<Value> n = root.next;
        while (n != null)
        {
             values.add(0, n.getValue());
             n = n.next;
        }
        return values;
    }

    // DATA TYPES //
    private class PHPIterator implements Iterator<Value>
    {
        private int current = -1;

        public boolean hasNext()
        {
            while(current < (M-1))
            {
                current++;
                if (pairs[current] != null) return true;
            }
            return false;
        }

        public Value next()
        {
            while (current < (M-1))
            {
                current++;
                if (pairs[current] != null) return pairs[current].getValue();
            }
            throw new NoSuchElementException();
        }
    }

    public static class Pair<V>
    {
        public V value;
        public String key;
    }

    public static class Node<V>
    {
        private Pair<V> pair;
        private Node<V> next;

        public Node(String k, V val)
        {
            pair = new Pair<V>();
            pair.key = k;
            pair.value = val;
        }

        public V getValue()
        {
            return pair.value;
        }

        public String getKey()
        {
            return pair.key;
        }

        public void setValue(V val)
        {
            pair.value = val;
        }

        public void setKey(String k)
        {
            pair.key = k;
        }
    }
}
