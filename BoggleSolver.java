package mwong.myprojects.boggle;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;

import mwong.algs4.resources.boggle.BoggleBoard;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation:  javac BoggleSolver26R.java
 *  Execution:  java BoggleSolver26R dictionary-algs4.txt board4x4.txt
 *  Dependencies (algs4.jar):  Bag.java, In.java, StdOut.java
 *  Dependencies:  BoggleBoard.java, BoggleBoardPlus26R.java, Boggle26WayRadixTrie.java
 *
 *  The test client takes the filename of a dictionary and the filename of 
 *  a Boggle board as command-line arguments and prints out all valid words 
 *  for the given board using the given dictionary.
 *
 ****************************************************************************/
 
public class BoggleSolver {
    private static final int OFFSET = BoggleDictionary.getOffset();
    private static final int IDX_Q = BoggleDictionary.getIdxQ();
    private BoggleDictionary trie;
    private Bag<String> words;
    private int[] faceIdx, nbrs;
    /**
     *  Initializes the data structure using the given array of strings as the dictionary.
     *  Assume each word in the dictionary contains only the uppercase 'A' to 'Z'
     *  
     *  @param dictionary String array of words in dictionary
     */
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Dictionnary is mandatory");
        }        
        trie = new BoggleDictionary();
        trie.loadDictionary(dictionary);
        words = new Bag<String>();
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
        if (board.rows() * board.cols() < 2  || trie.isEmpty()) {
            return new Bag<String>();
        }
        // increment the marker for new search
        trie.updateMarker();
        words = new Bag<String>();
        
        // load and convert the BoggleBoard to BoggleSolver properties 
        BoggleBoardPlus dices = new BoggleBoardPlus(board, OFFSET);
        // temporary set to negative negative during the search
        // restore it back when done for next search
        faceIdx = dices.getFaceIdx();
        //int[] faceChar = dices.getFaceChar();
        nbrs = dices.getNbrs();
        
        // Identify each pair of the first two characters, then use depth
        // first search to find all words exists in given dictionary
        for (int i = 0; i < faceIdx.length; i++) {
            int ch0 = faceIdx[i];
            faceIdx[i] = -1;
            int last = nbrs[i+1];
            for (int idx = nbrs[i]; idx < last; idx++) {
                int id = nbrs[idx];
                int ch1 = faceIdx[id];
                int key = trie.getNextKey(ch0 * 26 + ch1);
                if (ch0 == IDX_Q) {
                    String word = trie.getWordQ2(key);
                    if (word != null) {
                        words.add(word);
                    }
                    int revKey = trie.getNextKey(ch1 * 26 + ch0);
                    word = trie.getWordQ2(revKey);
                    if (word != null) {
                        words.add(word);
                    }
                }
                
                if (key > 0) {
                    faceIdx[id] = -1;
                    if (trie.hasRadix(key)) {
                        findWordsDFS(id, key, 
                        		trie.getRadixInit(key), trie.getRadixLength(key) - 1, trie.hasTrie(key));
                    } else {
                        findWordsDFS(id, key, -1, -1, true);
                    }
                    faceIdx[id] = ch1;
                }
            } 
            faceIdx[i] = ch0;
        }
        return words;        
    }
    
    // recursive depth first search the Boggle board to find all new words and
    // add to the words set
    private void findWordsDFS(int id, int key, int radixPos, int radixLength, boolean hasNextTrie) {
        int last = nbrs[id+1];
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
                    findWordsDFS(id2, nextKey, 
                            trie.getRadixInit(nextKey), trie.getRadixLength(nextKey) - 1, trie.hasTrie(nextKey));
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
    
    /**
     *  Returns the number of score of the given word if it is in the dictionary, 
     *      zero otherwise.  Assume the word contains only the uppercase 'A' to 'Z'
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
    
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        Stopwatch stopwatch = new Stopwatch();
    	BoggleSolver solver = new BoggleSolver(dictionary);
    	System.out.println(stopwatch.elapsedTime());
    	System.out.println("1d radix");
        
    	for (int k = 1; k < args.length; k++) {
        	System.out.println(args[k]);
        	BoggleBoard board = new BoggleBoard(args[k]);
        	//Stopwatch stopwatch2 = new Stopwatch();
        	//for (int j = 0; j < 20; j++) {
        		stopwatch = new Stopwatch();
        		int score = 0;
        		for (String word : solver.getAllValidWords(board)) {
        			score += solver.scoreOf(word);
        		}
        		System.out.println(score);
        		System.out.println(stopwatch.elapsedTime());
        	//}
        	//System.out.println(stopwatch2.elapsedTime() / 20);
        }
    }
}