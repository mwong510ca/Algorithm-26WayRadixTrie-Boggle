package mwong.myprojects.boggle;

/**
 * TrieInterface is the interface class that represents an trie data type of
 * key-value pairs with string keys and generic values and the standard methods.
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 *         www.github.com/mwong510ca/Boggle_TrieDataStructure
 */

public interface TrieInterface<T> {
    /**
     * Returns the generic type T associated with the given key.
     *
     * @param key the given String
     * @return generic type T  associated with the given key
     * @throws NullPointerException if key is null
     */
    T get(String key);

    /**
     * Returns the boolean value represents the given String key exists.
     *
     * @param key the given String
     * @return boolean value represents the given String key exists
     * @throws NullPointerException if key is null
     */
    boolean contains(String key);

    /**
     * Inserts the key-value pair into the trie, overwriting the old value with
     * the new value if the key is already in the trie.
     *
     * @param key the given String
     * @param val the generic type T
     * @throws NullPointerException if key is null
     */
    void put(String key, T val);

    /**
     * Removes the key from the trie if the key is present.
     *
     * @param key the given String
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
     * Returns the boolean value represents the trie is empty.
     *
     * @return boolean value represents the trie is empty
     */
    boolean isEmpty();

    /**
     * Returns all keys in the trie as an Iterable.
     *
     * @return all keys in the symbol table as an Iterable
     */
    Iterable<String> keys();
}
