package mwong.myprojects.boggle;

import java.util.ArrayList;

/**
 * BoggleSolver provide the function to find all words on the Boggle board
 * based on the given BoggleDictionary object.
 *
 * <p>Dependencies : BoggleBoard.java, BoggleBoardPlus.java, BoggleDictionary.java
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
*/
public class BoggleSolver {
    private static final int OFFSET = BoggleDictionary.getOffset();
    private static final int IDX_Q = BoggleDictionary.getIdxQ();
    private BoggleDictionary trie;
    private ArrayList<String> words;
    private int[] faceIdx;
    private int[] nbrs;
    private int[] doubleIdx;
    private boolean[] isDouble;

    /**
     *  Initializes the BoggleSolver with a given BoggleDictionary object.
     *
     *  @param dictionary the given BoggleDictionary object
     */
    public BoggleSolver(BoggleDictionary dictionary) {
        if (dictionary.isEmpty()) {
            throw new IllegalArgumentException("Dictionnary is mandatory");
        }
        trie = dictionary;
    }

    // set the dictionary with the given BoggleDictionary object
    void setDictionary(BoggleDictionary dictionary) {
        trie = dictionary;
    }

    /**
     *  Returns the set of all valid words in the given Boggle board, as an Iterable.
     *
     *  @param board the BoggleBoard object
     *  @return set of all valid words in the given Boggle board, as an Iterable
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Board is mandatory");
        }
        if (board.getSize() * board.getSize() < 2  || trie.isEmpty()) {
            return new ArrayList<String>();
        }
        // increment the marker for new search
        trie.updateMarker();
        words = new ArrayList<String>();

        // load and convert the BoggleBoard to BoggleSolver properties
        BoggleBoardPlus dices = new BoggleBoardPlus(board, OFFSET);
        // temporary set to negative negative during the search
        // restore it back when done for next search
        faceIdx = dices.getFaceIdx();
        nbrs = dices.getNbrs();
        isDouble = dices.isDouble();
        doubleIdx = dices.getDoubleIdx();
        boolean doubleBoard = false;
        for (boolean key : isDouble) {
            if (key) {
                doubleBoard = true;
                break;
            }
        }
        if (doubleBoard) {
            searchBoardDouble();
        } else {
            searchBoard();
        }
        return words;
    }

    // search the boggle board to find all words
    private void searchBoard() {
        // Identify each pair of the first two characters, then use depth
        // first search to find all words exists in given dictionary
        for (int i = 0; i < faceIdx.length; i++) {
            if (faceIdx[i] == -1) {
                continue;
            }

            int ch0 = faceIdx[i];
            faceIdx[i] = -1;
            int last = nbrs[i + 1];
            for (int idx = nbrs[i]; idx < last; idx++) {
                int id = nbrs[idx];
                int ch1 = faceIdx[id];
                int key = trie.getNextKey(ch0 * 26 + ch1);

                if (ch0 == IDX_Q) {
                    String word = trie.getWordQ2(key);
                    if (word != null) {
                        words.add(word);
                    }
                }

                if (key > 0) {
                    faceIdx[id] = -1;
                    if (trie.hasRadix(key)) {
                        findWordsDFS(id, key, trie.getRadixInit(key),
                                trie.getRadixLength(key) - 1, trie.hasTrie(key));
                    } else {
                        findWordsDFS(id, key, -1, -1, true);
                    }
                    faceIdx[id] = ch1;
                }
            }
            faceIdx[i] = ch0;
        }
    }

    // search the boggle board with double letters to find all words
    private void searchBoardDouble() {
        // Identify each pair of the first two characters, then use depth
        // first search to find all words exists in given dictionary
        for (int id0 = 0; id0 < faceIdx.length; id0++) {
            if (faceIdx[id0] == -1) {
                continue;
            }
            //System.out.println(id0);

            int ch0 = faceIdx[id0];
            if (isDouble[id0]) {
                int key = trie.getNextKey(ch0 * 26 + doubleIdx[id0]);
                if (key > 0) {
                    faceIdx[id0] = -1;
                    if (trie.hasRadix(key)) {
                        findWordsDFSDouble(id0, key, trie.getRadixInit(key),
                                trie.getRadixLength(key) - 1, trie.hasTrie(key));
                    } else {
                        findWordsDFSDouble(id0, key, -1, -1, true);
                    }
                    faceIdx[id0] = ch0;
                }
            } else {
                faceIdx[id0] = -1;
                int last = nbrs[id0 + 1];
                for (int idx = nbrs[id0]; idx < last; idx++) {
                    int id1 = nbrs[idx];
                    int ch1 = faceIdx[id1];
                    int key = trie.getNextKey(ch0 * 26 + ch1);
                    if (key > 0) {
                        if (isDouble[id1]) {
                            int ch2 = doubleIdx[id1];
                            if (trie.hasRadix(key)) {
                                int radixPos = trie.getRadixInit(key);
                                int remainLength = trie.getRadixLength(key) - 2;
                                if (ch2 == trie.getNextCharIndex(radixPos)) {
                                    if (remainLength > -1) {
                                        faceIdx[id1] = -1;
                                        findWordsDFSDouble(id1, key, radixPos + 1,
                                                remainLength, trie.hasTrie(key));
                                        faceIdx[id1] = ch1;
                                    } else {
                                        String word = trie.getWordRadix(key);
                                        if (word != null) {
                                            words.add(word);
                                            //System.out.println("\t + word");
                                        }
                                        if (trie.hasTrie(key)) {
                                            faceIdx[id1] = -1;
                                            findWordsDFSDouble(id1, key, -1, -1, true);
                                            faceIdx[id1] = ch1;
                                        }
                                    }
                                }
                            } else {
                                key = trie.getNextKey(key * 26 + ch2);
                                if (key > 0) {
                                    faceIdx[id1] = -1;
                                    if (trie.hasRadix(key)) {
                                        findWordsDFSDouble(id1, key, trie.getRadixInit(key),
                                                trie.getRadixLength(key) - 1, trie.hasTrie(key));
                                    } else {
                                        String word = trie.getWordRadix(key);
                                        if (word != null) {
                                            words.add(word);
                                            //System.out.println("\t + word");
                                        }
                                        if (trie.hasTrie(key)) {
                                            findWordsDFSDouble(id1, key, -1, -1, true);
                                        }
                                    }
                                    faceIdx[id1] = ch1;
                                }
                            }
                        } else {
                            if (ch0 == IDX_Q) {
                                String word = trie.getWordQ2(key);
                                if (word != null) {
                                    words.add(word);
                                    //System.out.println("\t + word");
                                }
                            }

                            faceIdx[id1] = -1;
                            if (trie.hasRadix(key)) {
                                findWordsDFSDouble(id1, key, trie.getRadixInit(key),
                                        trie.getRadixLength(key) - 1, trie.hasTrie(key));
                            } else {
                                findWordsDFSDouble(id1, key, -1, -1, true);
                            }
                            faceIdx[id1] = ch1;
                        }
                    }
                }
                faceIdx[id0] = ch0;
            }
        }
    }

    // recursive depth first search the Boggle board to find all new words and
    // add to the words set
    private void findWordsDFS(int id, int key, int radixPos, int radixLength, boolean hasNextTrie) {
        int last = nbrs[id + 1];
        int remainLength = radixLength - 1;
        if (radixPos < 0) {
            int refKey = key * 26;
            for (int idx = nbrs[id]; idx < last; idx++) {
                int id2 = nbrs[idx];
                int ch = faceIdx[id2];
                if (ch == -1 || trie.getNextKey(refKey + ch) == 0) {
                    continue;
                }
                int nextKey = trie.getNextKey(refKey + ch);
                if (trie.hasRadix(nextKey)) {
                    faceIdx[id2] = -1;
                    findWordsDFS(id2, nextKey, trie.getRadixInit(nextKey),
                            trie.getRadixLength(nextKey) - 1, trie.hasTrie(nextKey));
                    faceIdx[id2] = ch;
                } else {
                    String word = trie.getWord(nextKey);
                    if (word != null) {
                        words.add(word);
                    }
                    if (trie.hasTrie(nextKey)) {
                        faceIdx[id2] = -1;
                        findWordsDFS(id2, nextKey, -1, -1, true);
                        faceIdx[id2] = ch;
                    }
                }
            }
        } else {
            byte nextChar = trie.getNextCharIndex(radixPos);
            for (int idx = nbrs[id]; idx < last; idx++) {
                int id2 = nbrs[idx];
                int ch = faceIdx[id2];
                if (ch > -1 && ch == nextChar) {
                    if (remainLength > -1) {
                        faceIdx[id2] = -1;
                        findWordsDFS(id2, key, radixPos + 1, remainLength, hasNextTrie);
                        faceIdx[id2] = ch;
                    } else {
                        String word = trie.getWordRadix(key);
                        if (word != null) {
                            words.add(word);
                        }
                        if (hasNextTrie) {
                            faceIdx[id2] = -1;
                            findWordsDFS(id2, key, -1, -1, true);
                            faceIdx[id2] = ch;
                        }
                    }
                }
            }
        }
    }

    // recursive depth first search the Boggle board to find all new words and
    // add to the words set with double letters
    private void findWordsDFSDouble(int id, int key, int radixPos, int radixLength,
            boolean hasNextTrie) {
        int last = nbrs[id + 1];
        int remainLength = radixLength - 1;
        if (radixPos < 0) {
            int refKey = key * 26;
            for (int idx = nbrs[id]; idx < last; idx++) {
                int id2 = nbrs[idx];
                int ch = faceIdx[id2];
                if (ch == -1 || trie.getNextKey(refKey + ch) == 0) {
                    continue;
                }

                int nextKey = trie.getNextKey(refKey + ch);
                if (trie.hasRadix(nextKey)) {
                    if (isDouble[id2]) {
                        int radixPos2 = trie.getRadixInit(nextKey);
                        int remainLength2 = trie.getRadixLength(nextKey) - 2;
                        if (doubleIdx[id2] == trie.getNextCharIndex(radixPos2)) {
                            if (remainLength2 > -1) {
                                faceIdx[id2] = -1;
                                findWordsDFSDouble(id2, key, radixPos2 + 1, remainLength2,
                                        trie.hasTrie(nextKey));
                                faceIdx[id2] = ch;
                            } else {
                                String word = trie.getWordRadix(nextKey);
                                if (word != null) {
                                    words.add(word);
                                    //System.out.println("\t" + word);
                                }
                                if (trie.hasTrie(nextKey)) {
                                    faceIdx[id2] = -1;
                                    findWordsDFSDouble(id2, key, -1, -1, true);
                                    faceIdx[id2] = ch;
                                }
                            }
                        }
                    } else {
                        faceIdx[id2] = -1;
                        findWordsDFSDouble(id2, nextKey, trie.getRadixInit(nextKey),
                                trie.getRadixLength(nextKey) - 1, trie.hasTrie(nextKey));
                        faceIdx[id2] = ch;
                    }
                } else {
                    if (isDouble[id2] && trie.hasTrie(nextKey)) {
                        nextKey = trie.getNextKey(nextKey * 26 + doubleIdx[id2]);
                        if (trie.hasRadix(nextKey)) {
                            faceIdx[id2] = -1;
                            findWordsDFSDouble(id2, nextKey, trie.getRadixInit(nextKey),
                                    trie.getRadixLength(nextKey) - 1, trie.hasTrie(nextKey));
                            faceIdx[id2] = ch;
                        } else {
                            String word = trie.getWord(nextKey);
                            if (word != null) {
                                words.add(word);
                                //System.out.println("\t" + word);
                            }
                            if (nextKey > 0 && trie.hasTrie(nextKey)) {
                                faceIdx[id2] = -1;
                                findWordsDFSDouble(id2, nextKey, -1, -1, true);
                                faceIdx[id2] = ch;
                            }
                        }
                    } else if (!isDouble[id2]) {
                        String word = trie.getWord(nextKey);
                        if (word != null) {
                            words.add(word);
                            //System.out.println("\t" + word);
                        }
                        if (trie.hasTrie(nextKey)) {
                            faceIdx[id2] = -1;
                            findWordsDFSDouble(id2, nextKey, -1, -1, true);
                            faceIdx[id2] = ch;
                        }
                    }
                }
            }
        } else {
            byte nextChar = trie.getNextCharIndex(radixPos);
            for (int idx = nbrs[id]; idx < last; idx++) {
                int id2 = nbrs[idx];
                int ch = faceIdx[id2];
                if (ch > -1 && ch == nextChar) {
                    if (isDouble[id2]) {
                        if (remainLength > -1) {
                            if (doubleIdx[id2] == trie.getNextCharIndex(radixPos + 1)) {
                                if (remainLength - 1 > -1) {
                                    faceIdx[id2] = -1;
                                    findWordsDFSDouble(id2, key, radixPos + 2, remainLength - 1,
                                            hasNextTrie);
                                    faceIdx[id2] = ch;
                                } else {
                                    String word = trie.getWordRadix(key);
                                    if (word != null) {
                                        words.add(word);
                                    }
                                    if (hasNextTrie) {
                                        faceIdx[id2] = -1;
                                        findWordsDFSDouble(id2, key, -1, -1, true);
                                        faceIdx[id2] = ch;
                                    }
                                }
                            }
                        } else if (hasNextTrie) {
                            int nextKey = trie.getNextKey(key * 26 + doubleIdx[id2]);
                            if (nextKey > 0) {
                                if (trie.hasRadix(nextKey)) {
                                    faceIdx[id2] = -1;
                                    findWordsDFSDouble(id2, nextKey, trie.getRadixInit(nextKey),
                                            trie.getRadixLength(nextKey) - 1,
                                            trie.hasTrie(nextKey));
                                    faceIdx[id2] = ch;
                                } else {
                                    String word = trie.getWord(nextKey);
                                    if (word != null) {
                                        words.add(word);
                                    }
                                    if (trie.hasTrie(nextKey)) {
                                        faceIdx[id2] = -1;
                                        findWordsDFSDouble(id2, nextKey, -1, -1, true);
                                        faceIdx[id2] = ch;
                                    }
                                }
                            }
                        }
                    } else {
                        if (remainLength > -1) {
                            faceIdx[id2] = -1;
                            findWordsDFSDouble(id2, key, radixPos + 1, remainLength, hasNextTrie);
                            faceIdx[id2] = ch;
                        } else {
                            String word = trie.getWordRadix(key);
                            if (word != null) {
                                words.add(word);
                                //System.out.println("\t" + word);
                            }
                            if (hasNextTrie) {
                                faceIdx[id2] = -1;
                                findWordsDFSDouble(id2, key, -1, -1, true);
                                faceIdx[id2] = ch;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *  Returns the number of score of the given word if it is in the dictionary,
     *  zero otherwise.  Minimum 3 characters for 4 x 4 and 5 x 5 Boggle boards.
     *      length    scores
     *      0 – 2        0
     *      3 – 4        1
     *          5        2
     *          6        3
     *          7        5
     *         8+        11
     *  @param word the given string
     *  @return number of score of the given word if it is in the dictionary,
     *      zero otherwise
     */
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word is mandatory");
        }
        if (trie.contains(word)) {
            switch (word.length()) {
                case 3: case 4:
                    return 1;
                case 5:
                    return 2;
                case 6:
                    return 3;
                case 7:
                    return 5;
                case 8: default:
                    return 11;
            }
        }
        return 0;
    }

    /**
     *  Returns the number of score of the given word if it is in the dictionary,
     *  zero otherwise.  Minimum 4 characters for 6 x 6 Boggle boards.
     *      length    scores
     *      0 – 3        0
     *          4        1
     *          5        2
     *          6        3
     *          7        5
     *         8+        11 plus 2 per letter
     *  @param word the given string
     *  @return number of score of the given word if it is in the dictionary,
     *      zero otherwise
     */
    public int scoreOfBig(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word is mandatory");
        }
        if (trie.contains(word)) {
            int len = word.length();
            if (len < 3) {
                return 0;
            } else if (len < 8) {
                return scoreOf(word);
            } else {
                return (len - 8) * 2 + 11;
            }
        }
        return 0;
    }
}