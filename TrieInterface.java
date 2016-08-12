package mwong.myprojects.boggle;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation  : java TrieInterface.java
 *  
 *  TrieInterface class represents an trie data type of key-value pairs, with 
 *  string keys and generic values and it have the standard methods.
 *
 ****************************************************************************/

public interface TrieInterface<Value> {
    /**
     * Returns the value associated with the given key.
     * 
     * @param key the key
     * @return the value associated with the given key if the key is in the 
     *     trie and null if the key is not exists
     * @throws NullPointerException if key is null
     */
    Value get(String key);

    /**
     * Does this symbol table contain the given key?
     * 
     * @param key the key
     * @return true if this trie contains key and, false otherwise
     * @throws NullPointerException if key is null
     */
    boolean contains(String key);

    /**
     * Inserts the key-value pair into the trie, overwriting the old value with 
     * the new value if the key is already in the trie. 
     * 
     * @param key the key
     * @param val the value
     * @throws NullPointerException if key is null
     */
    void put(String key, Value val);

    /**
     * Removes the key from the trie if the key is present.
     * 
     * @param key the key
     * @throws NullPointerException if key is null
     */
    void delete(String key);

    /**
     * Returns the number of key-value pairs in this trie.
     * 
     * @return the number of key-value pairs in this trie
     */
    int size();

    /**
     * Is this trie empty?
     * 
     * @return true if this trie is empty and false otherwise
     */
    boolean isEmpty();

    /**
     * Returns all keys in the trie as an Iterable.
     * 
     * @return all keys in the symbol table as an <tt>Iterable</tt>
     */
    Iterable<String> keys();
}
