package mwong.myprojects.boggle;

import java.util.ArrayList;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac BoggleTrie26WayRadix.java
 *  Dependences: TrieInterface.java
 *
 *  A data type to trie structure of combination 26-way trie and radix trie
 *  stored in 2 dimension array, plus Qu filter for Boggle.
 *
 ****************************************************************************/

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
     *  default constructor, initial trie size of 256.
     */
    public BoggleTrie26WayRadix() { 
        this(256);
    }

    /**
     *  default constructor, with the given size.
     *  
     *  @param estSize the integer of given size of trie
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
     * Inserts the key into the trie, key is also the value if the 
     * key is already in the trie. 
     * 
     * @param key the key
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
     * Inserts the key-value pair into the trie, overwriting the old value with 
     * the new value if the key is already in the trie. 
     * 
     * @param key the key
     * @param value the value
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
        if (radixIdx + 64 > radix.length) {
            byte[] temp = new byte[radix.length * 2];
            System.arraycopy(radix, 0, temp, 0, radix.length);
            radix = temp;
        }
        
        int len = key.length();
        int d = 0;
        int ch0Idx = key.charAt(d++) - OFFSET;
        if (ch0Idx == IDX_Q) {
            if (key.charAt(d++) != 'U') {
                return;
            }
        }
        int ch1Idx = key.charAt(d++) - OFFSET;
        if (ch1Idx == IDX_Q) {
            if (d == len || key.charAt(d++) != 'U') {
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
            while (d < len) {
                radix[nextRdxIdx] = (byte) key.charAt(d++);
                if (radix[nextRdxIdx] == 'Q') {
                    if (d == len || key.charAt(d++) != 'U') {
                        d = len + 1;
                    }
                }
                nextRdxIdx++;
                count++;
            }
            
            if (d != len) {
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
        while (d < len) {
            // if trie index has radix, compare the matched characters until
            // end of radix
            int refRadixPos = 0;
            int refRadixLen = radixLength[trieIdx];
            if (refRadixLen > 0) {
                refRadixPos = radixPos[trieIdx];
                while (refRadixLen > 0 && d < len) {
                    if (key.charAt(d) == radix[refRadixPos]) {
                        d++;
                        if (radix[refRadixPos] == 'Q') {
                            if (d == len || key.charAt(d++) != 'U') {
                                d = len + 1;
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
            if (d >= len) {
                break;
            }

            int nextChIdx = key.charAt(d++) - OFFSET;
            if (nextChIdx == IDX_Q) {
                if (d == len || key.charAt(d++) != 'U') {
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
                    while (d < len) {
                        radix[nextRdxIdx] = (byte) key.charAt(d++);
                        if (radix[nextRdxIdx] == 'Q') {
                            if (d == len || key.charAt(d++) != 'U') {
                                d = len + 1;
                            }
                        }
                        nextRdxIdx++;
                        count++;
                    }
                        
                    if (d != len) {
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
                while (d < len) {
                    radix[nextRdxIdx] = (byte) key.charAt(d++);
                    if (radix[nextRdxIdx] == 'Q') {
                        if (d == len || key.charAt(d++) != 'U') {
                            d = len + 1;
                        }
                    }
                    nextRdxIdx++;
                    count++;
                }
                    
                if (d != len) {
                    break;
                }
                int splitIdx = trieCounter++;
                int newIdx = trieCounter++;
                trieR26[splitIdx] = trieR26[trieIdx];
                trieR26[trieIdx] = new int[26];
                trieR26[trieIdx][radix[refRadixPos] - OFFSET] = splitIdx;
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
     * Determine key if exists in this trie object.
     * 
     * @param key the key
     * @return true if this symbol table contains key, false otherwise
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
     * Returns the String value associated with the given key.
     * 
     * @param key the key
     * @return the value associated with the given key if the key is in the 
     *     trie and null if the key is not exists
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
        
        int d = 0;
        int len = key.length();
        
        int trieIdx = key.charAt(d++) - OFFSET;
        if (trieIdx == IDX_Q) {
            if (key.charAt(d++) != 'U') {
                return -1;
            }
        }
        int chIdx = key.charAt(d++) - OFFSET;
        trieIdx = trieR26[trieIdx][chIdx];
        
        while (trieIdx > 0) {
            if (chIdx == IDX_Q) {
                if (!(d < len && key.charAt(d++) == 'U')) {
                    return -1;
                }
            }
            
            int radixLen = radixLength[trieIdx];
            if (radixLen > 0) {
                // Notes: radix storage store the ASCII code
                int pos = radixPos[trieIdx];
                do {
                    if (d >= len) {
                        return -1;
                    }
                    int ascii = key.charAt(d++);
                    if (ascii != radix[pos++]) {
                        return -1;
                    }
                    if (ascii == 'Q') {
                        if (!(d < len && key.charAt(d++) == 'U')) {
                            return -1;
                        }
                    }
                    radixLen--;
                } while (radixLen > 0);
                
                if (d == len) {
                    if (values[trieIdx] == null) {
                        return -1;
                    }
                    return trieIdx;
                }
            } else {
                if (d == len) {
                    if (values[trieIdx] == null) {
                        return -1;
                    }
                    return trieIdx;
                }
            }
            
            // continue search
            if (trieR26[trieIdx] != null) {
                chIdx = key.charAt(d++) - OFFSET;
                trieIdx = trieR26[trieIdx][chIdx];        
            } else {
                return -1;
            }
        }
        return -1;
    }
            
    /**
     * Doesn't implement remove() since it's optional.
     * 
     * @param key the key
     * @throws UnsupportedOperationException optional function not in use.
     */
    public void delete(String key) {
         throw new UnsupportedOperationException(); 
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    public int size() {
        return size;
    }

    /**
     * Is this symbol table empty?
     * @return <tt>true</tt> if this symbol table is empty and <tt>false</tt> otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns all keys in the symbol table as an <tt>Iterable</tt>.
     * To iterate over all of the keys in the symbol table named <tt>st</tt>,
     * use the foreach notation: <tt>for (Key key : st.keys())</tt>.
     * @return all keys in the symbol table as an <tt>Iterable</tt>
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
    
    static final int getOffset() {
        return OFFSET;
    }

    static final int getIdxQ() {
        return IDX_Q;
    }

    final byte[] getRadix() {
        return radix;
    }

    final byte[] getRadixLength() {
        return radixLength;
    }

    final int[] getRadixPos() {
        return radixPos;
    }

    final int[][] getTrieR26() {
        return trieR26;
    }

    final String[] getValues() {
        return values;
    }

    final int getTrieCounter() {
        return trieCounter;
    }

    final int getRadixSize() {
        return radixIdx;
    }
}
