package mwong.myprojects.boggle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

/**
 * BoggleDictionary is the data type of dictionary for Boggle game.  It loads a list
 * of word from DictionaryOption or given filepath.  It convert into trie structure
 * and sort in dictionary order.
 *
 * <p>Dependencies : DictionaryOptions.java
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 */
public class BoggleDictionary {
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int OFFSET = BoggleTrie26WayRadix.getOffset();
    private static final int IDX_Q = BoggleTrie26WayRadix.getIdxQ();
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static String directory = "dictionary";
    private static final DictionaryOptions defaultDictionary = DictionaryOptions.EOWL;

    private byte[] radix;
    private byte[] radixLength;
    private int[] radixIdx;
    private int[] trieR26;
    private String[] words;
    private int[] visited;
    private boolean empty;
    private int hasWordGroup1;
    private int endTrie;
    private int hasWordGroup2;
    private int hasRadix;
    private int endRadix;
    private int marker;

    /**
     *  Initializes the BoggleDictionary object using default dictionary.
     */
    public BoggleDictionary() {
        this(defaultDictionary);
    }

    /**
     * Initializes the BoggleDictionary object using the given dictionary option.
     *
     * @param option the given dictionary option
     */
    public BoggleDictionary(DictionaryOptions option) {
        String filepath = directory + SEPARATOR + option.getFilename();
        loadDictionary(filepath);
    }

    /**
     * Initializes the BoggleDictionary object using the give dictionary file.
     *
     * @param filepath the given String of file path
     */
    public BoggleDictionary(String filepath) {
        loadDictionary(filepath);
    }

    // load and sort all dictionary words in order
    private String[] readDictionary(String filepath) {
        empty = true;
        marker = -1;
        File file = new File(filepath);
        if (!file.exists()) {
            System.out.println(filepath + " not found.");
            return null;
        }

        TreeSet<String> lines = new TreeSet<String>();
        try (BufferedReader buf = new BufferedReader(new FileReader(filepath))) {
            String line = buf.readLine();
            while (line != null) {
                line = line.trim().toUpperCase();
                if (line.length() > 2) {
                    boolean skip = false;
                    for (int idx = 0; idx < line.length(); idx++) {
                        if (alphabet.indexOf(line.charAt(idx)) == -1) {
                            skip = true;
                            break;
                        }
                    }
                    if (!skip) {
                        lines.add(line);
                    }
                }
                line = buf.readLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        if (lines.size() == 0) {
            return null;
        }

        String[] dict = new String[lines.size()];
        int idx = 0;
        for (String str : lines) {
            dict[idx++] = str;
        }
        return dict;
    }

    // Read all words from the dictionary file and load into trie object,
    // retrieve trie components and reorder it and set a flag of each group of words.
    private void loadDictionary(String filepath) {
        String[] dict = readDictionary(filepath);
        if (dict == null) {
            dict = BoggleDictionaryDefault.getWord();
        }

        // load all words in trie the retrieve it's components
        BoggleTrie26WayRadix trie = new BoggleTrie26WayRadix(dict.length);
        for (String key : dict) {
            if (key != null && key.length() > 2) {
                trie.put(key);
            }
        }

        if (trie.isEmpty()) {
            empty = true;
            return;
        }
        empty = false;
        int[][] tempTrie26 = trie.getTrieR26();
        int[] tempRadixPos = trie.getRadixPos();
        String[] tempWords  = trie.getValues();
        int trieCounter = trie.getTrieCounter();

        // count the entries as describe below:
        //          Has next trie   is a word   has radix       flags
        // group1      Yes            No          No            hasWord1
        // group2      Yes            Yes         No            hasRadix
        // group3      Yes            No          Yes           hasWord2
        // group4      Yes            Yes         Yes           endTrie
        // group5      No             Yes         Yes           endRadix
        // remaining   No             Yes         No
        int group1 = 0;
        int group2 = 0;
        int group3 = 0;
        int group4 = 0;
        int group5 = 0;
        for (int i = 26; i < trieCounter; i++) {
            if (tempTrie26[i] != null) {
                if (tempRadixPos[i] > 0) {
                    if (tempWords[i] == null) {
                        group3++;
                    } else {
                        group4++;
                    }
                } else {
                    if (tempWords[i] == null) {
                        group1++;
                    } else {
                        group2++;
                    }
                }
            } else if (tempRadixPos[i] > 0) {
                group5++;
            }
        }

        // initial the reset value by group, reorder the whole trie,
        // copy radix storage, and set the flags/indicators
        int reset1 = 26;
        int reset2 = reset1 + group1;
        hasWordGroup1 = reset2 - 1;
        int reset3 = reset2 + group2;
        hasRadix = reset3 - 1;
        int reset4 = reset3 + group3;
        hasWordGroup2 = reset4 - 1;
        int reset5 = reset4 + group4;
        endTrie = reset5;
        int reset6 = reset5 + group5;
        endRadix = reset6;

        int[][]sortedTrie26 = new int[trieCounter][26];
        words = new String[trieCounter - hasWordGroup1];
        visited = new int[trieCounter - hasWordGroup1];
        radix = new byte[trie.getRadixSize()];
        System.arraycopy(trie.getRadix(), 0, radix, 0, trie.getRadixSize());
        radixIdx = new int[reset6 - reset3 + 1];
        radixLength = new byte[reset6 - reset3 + 1];
        byte[] tempRadixLength = trie.getRadixLength();

        int[] ref = new int[trieCounter];
        for (int trieIdx = 0; trieIdx < trieCounter; trieIdx++) {
            if (tempTrie26[trieIdx] != null) {
                for (int chIdx = 0; chIdx < 26; chIdx++) {
                    int nextTrieIdx = tempTrie26[trieIdx][chIdx];

                    if (nextTrieIdx > 0) {
                        if (tempTrie26[nextTrieIdx] != null) {
                            if (tempRadixPos[nextTrieIdx] == 0) {
                                if (tempWords[nextTrieIdx] == null) {
                                    // 1st set:    Trie-Y    Word-N    Radix-N
                                    sortedTrie26[trieIdx][chIdx] = reset1;
                                    ref[reset1++] = nextTrieIdx;
                                } else {
                                    // 2nd set:    Trie-Y    Word-Y    Radix-N
                                    sortedTrie26[trieIdx][chIdx] = reset2;
                                    words[reset2 - hasWordGroup1] = tempWords[nextTrieIdx];
                                    ref[reset2++] = nextTrieIdx;
                                }
                            } else {
                                if (tempWords[nextTrieIdx] == null) {
                                    // 3rd set:    Trie-Y    Word-N    Radix-Y
                                    sortedTrie26[trieIdx][chIdx] = reset3;
                                    radixIdx[reset3 - hasRadix] = tempRadixPos[nextTrieIdx];
                                    radixLength[reset3 - hasRadix] = tempRadixLength[nextTrieIdx];
                                    ref[reset3++] = nextTrieIdx;
                                } else {
                                    // 4th set:    Trie-Y    Word-Y    Radix-Y
                                    sortedTrie26[trieIdx][chIdx] = reset4;
                                    radixIdx[reset4 - hasRadix] = tempRadixPos[nextTrieIdx];
                                    radixLength[reset4 - hasRadix] = tempRadixLength[nextTrieIdx];
                                    words[reset4 - hasWordGroup1] = tempWords[nextTrieIdx];
                                    ref[reset4++] = nextTrieIdx;
                                }
                            }
                        } else {
                            if (tempRadixPos[nextTrieIdx] > 0) {
                                // 5th set:    Trie-N    Word-Y    Radix-Y
                                sortedTrie26[trieIdx][chIdx] = reset5;
                                radixIdx[reset5 - hasRadix] = tempRadixPos[nextTrieIdx];
                                radixLength[reset5 - hasRadix] = tempRadixLength[nextTrieIdx];
                                words[reset5 - hasWordGroup1] = tempWords[nextTrieIdx];
                                ref[reset5++] = nextTrieIdx;
                            } else {
                                // 6th set:    Trie-N    Word-N    Radix-N
                                sortedTrie26[trieIdx][chIdx] = reset6;
                                words[reset6 - hasWordGroup1] = tempWords[nextTrieIdx];
                                ref[reset6++] = nextTrieIdx;
                            }
                        }
                    }
                }
            }
        }

        // copy the sorted order trie to final copy in 1 dimension array
        trieR26 = new int[endTrie * 26];
        for (int i = 0; i < 26; i++) {
            System.arraycopy(sortedTrie26[i], 0, trieR26, i * 26, 26);
        }
        for (int i = 26; i < endTrie; i++) {
            System.arraycopy(sortedTrie26[ref[i]], 0, trieR26, i * 26, 26);
        }
    }

    /**
     *  Returns the number of the index of character 'Q'.
     *
     *  @return number of the index of character 'Q'
     */
    public static int getIdxQ() {
        return IDX_Q;
    }

    /**
     *  Returns the number of offset value of character.
     *
     *  @return number of offset value of character
     */
    public static int getOffset() {
        return OFFSET;
    }

    /**
     *  Returns the boolean represent the trie is empty.
     *
     *  @return boolean represent the trie is empty
     */
    protected boolean isEmpty() {
        return empty;
    }

    /**
     *  Returns the boolean represent the trie contains the given string key.
     *
     *  @param key the given string
     *  @return boolean represent the trie contains the given string key
     */
    protected boolean contains(String key) {
        if (key.isEmpty() || key.length() < 3) {
            return false;
        }
        int depth = 0;
        int len = key.length();

        int trieIdx = key.charAt(depth++) - OFFSET;
        if (trieIdx == IDX_Q) {
            if (key.charAt(depth++) != 'U') {
                return false;
            }
        }
        int chIdx = key.charAt(depth++) - OFFSET;
        trieIdx = trieR26[trieIdx * 26 + chIdx];

        while (trieIdx > 0) {
            if (chIdx == IDX_Q) {
                if (!(depth < len && key.charAt(depth++) == 'U')) {
                    return false;
                }
            }

            if (trieIdx > hasRadix && trieIdx < endRadix) {
                // Notes: radix storage store the ASCII code
                int pos = radixIdx[trieIdx - hasRadix];
                int radixLen = radixLength[trieIdx - hasRadix] - 1;
                do {
                    if (depth >= len) {
                        return false;
                    }
                    int ascii = key.charAt(depth++);
                    if (ascii != radix[pos++]) {
                        return false;
                    }
                    if (ascii == 'Q') {
                        if (!(depth < len && key.charAt(depth++) == 'U')) {
                            return false;
                        }
                    }
                } while (--radixLen > -1);
                if (depth == len) {
                    return trieIdx > hasWordGroup2;
                }
            } else {
                if (depth == len) {
                    return trieIdx > hasWordGroup1;
                }
            }

            // continue search
            if (trieIdx < endTrie) {
                chIdx = key.charAt(depth++) - OFFSET;
                trieIdx = trieR26[trieIdx * 26 + chIdx];
            } else {
                return false;
            }
        }
        return false;
    }

    protected void updateMarker() {
        marker += 2;
    }

    TreeSet<String> getAllWords(int minLength) {
        TreeSet<String> set = new TreeSet<String>();
        for (String str : words) {
            if (str != null && str.length() >= minLength) {
                set.add(str);
            }
        }
        return set;
    }

    /**
     *  Returns the number of next key in trie, 0 if not exists.
     *
     *  @param key the number of current key
     *  @return number of next key in trie, 0 if not exists
     */
    protected int getNextKey(int key) {
        return trieR26[key];
    }

    /**
     *  Returns the boolean represent the key has next trie set.
     *
     *  @param key the number of current key
     *  @return boolean represent the key has next trie set
     */
    protected boolean hasTrie(int key) {
        return key < endTrie;
    }

    /**
     *  Returns the boolean represent the key has radix.
     *
     * @param key the number of current key
     *  @return boolean represent the key has radix
     */
    protected boolean hasRadix(int key) {
        return key > hasRadix && key < endRadix;
    }

    /**
     *  Returns the number of radix index of radix storage.
     *
     *  @param key the number of current key
     *  @return number of radix index of radix storage
     */
    protected int getRadixInit(int key) {
        return radixIdx[key - hasRadix];
    }

    /**
     *  Returns the number of radix length.
     *
     *  @param key the number of current key
     *  @return number of radix length
     */
    protected int getRadixLength(int key) {
        return radixLength[key - hasRadix];
    }

    /**
     *  Returns the byte represent the ith radix character index.
     *
     *  @param pos the number of current radix index of radix storage
     *  @return byte represent the ith radix character index
     */
    protected byte getNextCharIndex(int pos) {
        return (byte) (radix[pos] - OFFSET);
    }

    /**
     *  Returns the boolean represent the key with radix is not a visited word.
     *
     *  @param key the number of current key with radix
     *  @param marker the number of current marker
     *  @return boolean represent the key with radix is not a visited word
     */
    protected String getWordRadix(int key) {
        if (key <= hasWordGroup2) {
            return null;
        }
        int refKey = key - hasWordGroup1;
        if (visited[refKey] == marker) {
            return null;
        }
        visited[refKey] = marker;
        return words[refKey];
    }

    /**
     *  Returns the boolean represent the key without radix is not a visited word.
     *
     *  @param key the number of current key without radix
     *  @param marker the number of current marker
     *  @return boolean represent the key without radix is not a visited word
     */
    protected String getWord(int key) {
        int refKey = key - hasWordGroup1;
        if (refKey <= 0) {
            return null;
        }
        if (visited[refKey] == marker) {
            return null;
        }
        visited[refKey] = marker;
        return words[refKey];
    }

    /**
     *  Returns the boolean represent the key of 2 boggle dices with Qu.
     *
     *  @param key the number of current key of 2 boggle dices with Qu
     *  @param marker the number of current marker
     *  @return boolean represent the key of 2 boggle dices with Qu is not a visited word
     */
    protected String getWordQ2(int key) {
        if (key > hasRadix) {
            return null;
        }
        return getWord(key);
    }
}
