package mwong.myprojects.boggle;

import java.util.ArrayList;

/**
 * BoggleTrie26WayRadix is the trie structure of combination 26-way trie and radix trie
 *  stored in 2 dimension array, plus Qu filter for Boggle game.
 *
 * <p>Dependencies : TrieInterface.java
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 */
public class BoggleTrie26WayRadix implements TrieInterface<String> {
    private static final int OFFSET = 'A';
    private static final int IDX_Q = 'Q' - OFFSET;
    private byte[] radix;
    private byte[] radixLength;
    private int[] radixPos;
    private int[][] trieR26;
    private String[] values;
    private int size = 0;
    private int trieCounter;
    private int radixIdx;

    /**
     * Initialize the BoggleTrie26WayRadix with default size 256 for first 2 characters.
     */
    public BoggleTrie26WayRadix() {
        this(256);
    }

    /**
     *  Initialize the BoggleTrie26WayRadix with with the given size.
     *
     *  @param estSize the integer of given estimate size of trie
     */
    public BoggleTrie26WayRadix(int estSize) {
        // add 26 reserve for first character from A to Z.
        int estCapacity = (estSize + 26) * 2;
        radix = new byte[estCapacity];
        radixLength = new byte[estCapacity];
        radixPos = new int[estCapacity];
        trieR26 = new int[estCapacity][];
        values  = new String[estCapacity];
        trieCounter = 26;
        radixIdx = 1;
        for (int i = 0; i < 26; i++) {
            trieR26[i] = new int[26];
        }
    }

    /**
     * Inserts the key into the trie, original key is the value if the
     * key is already in the trie.
     *
     * @param key the given String
     * @throws NullPointerException if key is null
     */
    protected void put(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (key.length() < 3) {
            return;
        }

        put(key, key);
    }

    /**
     * Inserts the key-value pair into the trie.  Ignore the entry of duplicate key
     * or  key with 'Q' but not follow by 'U', and eliminate 'U' from 'QU' double
     * letters in any keys.
     *
     * @param key the given String
     * @param value the given String
     * @throws NullPointerException if key is null
     */
    public void put(String key, String value) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (key.length() < 3) {
            return;
        }
        if (trieCounter + 2 > trieR26.length) {
            int len = trieR26.length;
            int doubleSize = trieR26.length * 2;

            int[][] tempR26 = new int[doubleSize][];
            System.arraycopy(trieR26, 0, tempR26, 0, len);
            trieR26 = tempR26;

            byte[] tempRLen = new byte[doubleSize];
            System.arraycopy(radixLength, 0, tempRLen, 0, len);
            radixLength = tempRLen;

            int[] tempRPos = new int[doubleSize];
            System.arraycopy(radixPos, 0, tempRPos, 0, len);
            radixPos = tempRPos;

            String[] tempVal = new String[doubleSize];
            System.arraycopy(values, 0, tempVal, 0, len);
            values = tempVal;
        }
        // double the radix storage size if remaining size less then 64 characters
        if (radixIdx + 64 > radix.length) {
            byte[] temp = new byte[radix.length * 2];
            System.arraycopy(radix, 0, temp, 0, radix.length);
            radix = temp;
        }

        int len = key.length();
        int depth = 0;
        int ch0Idx = key.charAt(depth++) - OFFSET;
        if (ch0Idx == IDX_Q) {
            if (key.charAt(depth++) != 'U') {
                return;
            }
        }
        int ch1Idx = key.charAt(depth++) - OFFSET;
        if (ch1Idx == IDX_Q) {
            if (depth == len || key.charAt(depth++) != 'U') {
                return;
            }
        }

        // determine the trie index based on the first two characters
        // if it is empty, assign a new index and insert new key and copy radix
        // Notes: radix storage store the ASCII code
        int trieIdx = trieR26[ch0Idx][ch1Idx];
        if (trieIdx == 0) {
            byte count = 0;
            int nextRdxIdx = radixIdx;
            while (depth < len) {
                radix[nextRdxIdx] = (byte) key.charAt(depth++);
                if (radix[nextRdxIdx] == 'Q') {
                    if (depth == len || key.charAt(depth++) != 'U') {
                        depth = len + 1;
                    }
                }
                nextRdxIdx++;
                count++;
            }

            if (depth != len) {
                return;
            }

            int newIdx = trieCounter++;
            trieR26[ch0Idx][ch1Idx] = newIdx;
            values[newIdx] = value;
            if (count == 0) {
                radixPos[newIdx] = 0;
                radixLength[newIdx] = 0;
            } else {
                radixPos[newIdx] = radixIdx;
                radixLength[newIdx] = count;
                radixIdx = nextRdxIdx;
            }
            size++;
            return;
        }

        // continue to loop through every character of the key
        while (depth < len) {
            // if trie index has radix, compare the matched characters until
            // end of radix
            int refRadixPos = 0;
            int refRadixLen = radixLength[trieIdx];
            if (refRadixLen > 0) {
                refRadixPos = radixPos[trieIdx];
                while (refRadixLen > 0 && depth < len) {
                    if (key.charAt(depth) == radix[refRadixPos]) {
                        depth++;
                        if (radix[refRadixPos] == 'Q') {
                            if (depth == len || key.charAt(depth++) != 'U') {
                                depth = len + 1;
                                break;
                            }
                        }
                        refRadixPos++;
                        refRadixLen--;
                    } else {
                        break;
                    }
                }
            }

            // if search depth equal key's length, duplicate key
            // break and insert next key
            if (depth == len) {
                break;
            }

            int nextChIdx = key.charAt(depth++) - OFFSET;
            if (nextChIdx == IDX_Q) {
                if (depth == len || key.charAt(depth++) != 'U') {
                    break;
                }
            }

            // matched at end of radix or no radix, if next trie index
            // is empty, insert new key and copy radix
            // Notes: radix storage store the ASCII code
            if (refRadixPos == 0 || refRadixLen == 0) {
                if (trieR26[trieIdx] == null) {
                    trieR26[trieIdx] = new int[26];
                }

                if (trieR26[trieIdx][nextChIdx] == 0) {
                    byte count = 0;
                    int nextRdxIdx = radixIdx;
                    while (depth < len) {
                        radix[nextRdxIdx] = (byte) key.charAt(depth++);
                        if (radix[nextRdxIdx] == 'Q') {
                            if (depth == len || key.charAt(depth++) != 'U') {
                                depth = len + 1;
                            }
                        }
                        nextRdxIdx++;
                        count++;
                    }

                    if (depth != len) {
                        break;
                    }

                    int newIdx = trieCounter++;
                    trieR26[trieIdx][nextChIdx] = newIdx;
                    values[newIdx] = value;
                    if (count == 0) {
                        radixPos[newIdx] = 0;
                        radixLength[newIdx] = 0;
                    } else {
                        radixPos[newIdx] = radixIdx;
                        radixLength[newIdx] = count;
                        radixIdx = nextRdxIdx;
                    }
                    size++;
                    break;
                }
                // otherwise, continue search trie index
                trieIdx = trieR26[trieIdx][nextChIdx];
            } else {
                // stop in the middle of reference radix, split in half,
                // assign new trie indexes and update radix position if needed;
                // then insert new key and copy radix
                // Notes: radix storage store the ASCII code
                byte count = 0;
                int nextRdxIdx = radixIdx;
                // screen the rest substring and check QU requirement
                while (depth < len) {
                    radix[nextRdxIdx] = (byte) key.charAt(depth++);
                    if (radix[nextRdxIdx] == 'Q') {
                        if (depth == len || key.charAt(depth++) != 'U') {
                            depth = len + 1;
                        }
                    }
                    nextRdxIdx++;
                    count++;
                }

                if (depth != len) {
                    break;
                }
                int splitIdx = trieCounter++;
                trieR26[splitIdx] = trieR26[trieIdx];
                trieR26[trieIdx] = new int[26];
                trieR26[trieIdx][radix[refRadixPos] - OFFSET] = splitIdx;
                int newIdx = trieCounter++;
                trieR26[trieIdx][nextChIdx] = newIdx;
                values[splitIdx] = values[trieIdx];
                values[trieIdx] = null;
                values[newIdx] = value;

                byte rest = (byte) (radixLength[trieIdx] - (refRadixPos - radixPos[trieIdx]) - 1);
                if (rest > 0) {
                    radixPos[splitIdx] = refRadixPos + 1;
                    radixLength[splitIdx] = rest;
                }

                radix[refRadixPos] = -1;
                radixLength[trieIdx] = (byte) (refRadixPos - radixPos[trieIdx]);
                if (radixLength[trieIdx] == 0) {
                    radixPos[trieIdx] = 0;
                }
                if (count == 0) {
                    radixPos[newIdx] = 0;
                    radixLength[newIdx] = 0;
                } else {
                    radixPos[newIdx] = radixIdx;
                    radixLength[newIdx] = count;
                    radixIdx = nextRdxIdx;
                }
                size++;
                break;
            }
        }
    }

    /**
     * Returns the boolean value represents the given String key exists.
     *
     * @param key the given String
     * @return boolean value represents the given String key exists
     * @throws NullPointerException if key is null
     */
    public boolean contains(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (key.length() < 3) {
            return false;
        }
        if (getIndex(key) == -1) {
            return false;
        }
        return true;
    }

    /**
     * Returns the String of original word associated with the given key.
     *
     * @param key the given String
     * @return String of original word associated with the given key.
     * @throws NullPointerException if key is null
     */
    public String get(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (key.length() < 3) {
            return null;
        }

        int refIndex = getIndex(key);
        if (refIndex == -1) {
            return null;
        }
        return values[refIndex];
    }

    // Return the trie index referenced to the given key, -1 if not exists.
    private int getIndex(String key) {
        if (key.isEmpty() || key.length() < 3) {
            return -1;
        }

        int depth = 0;
        int len = key.length();

        int trieIdx = key.charAt(depth++) - OFFSET;
        if (trieIdx == IDX_Q) {
            if (key.charAt(depth++) != 'U') {
                return -1;
            }
        }
        int chIdx = key.charAt(depth++) - OFFSET;
        trieIdx = trieR26[trieIdx][chIdx];

        while (trieIdx > 0) {
            if (chIdx == IDX_Q) {
                if (!(depth < len && key.charAt(depth++) == 'U')) {
                    return -1;
                }
            }

            int radixLen = radixLength[trieIdx];
            if (radixLen > 0) {
                // Notes: radix storage store the ASCII code
                int pos = radixPos[trieIdx];
                do {
                    if (depth >= len) {
                        return -1;
                    }
                    int ascii = key.charAt(depth++);
                    if (ascii != radix[pos++]) {
                        return -1;
                    }
                    if (ascii == 'Q') {
                        if (!(depth < len && key.charAt(depth++) == 'U')) {
                            return -1;
                        }
                    }
                    radixLen--;
                } while (radixLen > 0);

                if (depth == len) {
                    if (values[trieIdx] == null) {
                        return -1;
                    }
                    return trieIdx;
                }
            } else {
                if (depth == len) {
                    if (values[trieIdx] == null) {
                        return -1;
                    }
                    return trieIdx;
                }
            }

            // continue search
            if (trieR26[trieIdx] != null) {
                chIdx = key.charAt(depth++) - OFFSET;
                trieIdx = trieR26[trieIdx][chIdx];
            } else {
                return -1;
            }
        }
        return -1;
    }

    /**
     * Uusupported delete() function.
     *
     * @param key the given String
     * @throws UnsupportedOperationException optional function not in use.
     */
    public void delete(String key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the number of key-value pairs in this trie.
     *
     * @return the number of key-value pairs in this trie
     */
    public int size() {
        return size;
    }

    /**
     * Returns the boolean value represents the trie is empty.
     *
     * @return boolean value represents the trie is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns all original keys in the trie as an Iterable.
     *
     * @return all original keys in the symbol table as an Iterable
     */
    public Iterable<String> keys() {
        ArrayList<String> list = new ArrayList<String>(size);
        for (String str : values) {
            if (str != null && !str.isEmpty()) {
                list.add(str);
            }
        }
        return list;
    }

    // return the offset value of trie
    static final int getOffset() {
        return OFFSET;
    }

    // return the index of character 'Q'
    static final int getIdxQ() {
        return IDX_Q;
    }

    // return the byte array of radix storage
    final byte[] getRadix() {
        return radix;
    }

    // return the byte array of radix length associated with trie indexes
    final byte[] getRadixLength() {
        return radixLength;
    }

    // return the integer array of radix index associated with trie indexes
    final int[] getRadixPos() {
        return radixPos;
    }

    // return the two dimensional integer array of trie storage
    final int[][] getTrieR26() {
        return trieR26;
    }

    // return the String array of original key associated with trie indexes
    final String[] getValues() {
        return values;
    }

    // return the integer value of the trie counter
    final int getTrieCounter() {
        return trieCounter;
    }

    // return the integer value of the radix size
    final int getRadixSize() {
        return radixIdx;
    }
}
