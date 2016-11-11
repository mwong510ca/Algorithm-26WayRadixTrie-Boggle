package mwong.myprojects.boggle;

import java.util.ArrayList;

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
    private ArrayList<String> words;
    private int[] faceIdx, nbrs, comboIdx;
    private boolean[] combo;
    /**
     *  Initializes the data structure using the given array of strings as the dictionary.
     *  Assume each word in the dictionary contains only the uppercase 'A' to 'Z'
     *  
     *  @param dictionary String array of words in dictionary
     */
    public BoggleSolver(BoggleDictionary dictionary) {
        if (dictionary.isEmpty()) {
            throw new IllegalArgumentException("Dictionnary is mandatory");
        }     
        trie = dictionary;
    }
    
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
        if (board.rows() * board.cols() < 2  || trie.isEmpty()) {
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
        combo = dices.getCombo();
        comboIdx = dices.getComboIdx();
        boolean comboBoard = false;
        for (boolean key : combo) {
        	if (key) {
        		comboBoard = true;
        		break;
        	}
        }
        if (comboBoard) {
        	getAllValidWordsCombo();
        } else {
        	getAllValidWords();
        }
        return words;        
    }
    
	private void getAllValidWords() {
		// Identify each pair of the first two characters, then use depth
        // first search to find all words exists in given dictionary
        for (int i = 0; i < faceIdx.length; i++) {
        	if (faceIdx[i] == -1) {
        		continue;
        	}
        	
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
	}
	
    /**
     *  Returns the set of all valid words in the given Boggle board, as an Iterable.
     *  
     *  @param board the BoggleBoard object
     *  @return set of all valid words in the given Boggle board, as an Iterable
     */
    private void getAllValidWordsCombo() {
       // Identify each pair of the first two characters, then use depth
        // first search to find all words exists in given dictionary
        for (int id0 = 0; id0 < faceIdx.length; id0++) {
        	if (faceIdx[id0] == -1) {
        		continue;
        	}
        	//System.out.println(id0);
            
        	int ch0 = faceIdx[id0];
        	if (combo[id0]) {
        		int key = trie.getNextKey(ch0 * 26 + comboIdx[id0]);
        		if (key > 0) {
        			faceIdx[id0] = -1;
        			if (trie.hasRadix(key)) {
                        findWordsDFSCombo(id0, key, 
                        		trie.getRadixInit(key), trie.getRadixLength(key) - 1, trie.hasTrie(key));
                    } else {
                        findWordsDFSCombo(id0, key, -1, -1, true);
                    }
        			faceIdx[id0] = ch0;
        		}        		
        	} else {
        		faceIdx[id0] = -1;
                int last = nbrs[id0+1];
                for (int idx = nbrs[id0]; idx < last; idx++) {
                    int id1 = nbrs[idx];
                    int ch1 = faceIdx[id1];
                    int key = trie.getNextKey(ch0 * 26 + ch1);
                    if (key > 0) {
                    	if (combo[id1]) {
                        	int ch2 = comboIdx[id1];
                        	if (trie.hasRadix(key)) {
                        		int radixPos = trie.getRadixInit(key);
                        		int remainLength = trie.getRadixLength(key) - 2;
                        		if (ch2 == trie.getNextCharIndex(radixPos)) {
                        			if (remainLength > -1) {
                                        faceIdx[id1] = -1;
                                        findWordsDFSCombo(id1, key, radixPos + 1, remainLength, trie.hasTrie(key));
                                        faceIdx[id1] = ch1;
                                    } else {
                                        String word = trie.getWordRadix(key);
                                        if (word != null) {
                                            words.add(word);
                                            //System.out.println("\t + word");
                                        }
                                        if (trie.hasTrie(key)) {
                                            faceIdx[id1] = -1;
                                            findWordsDFSCombo(id1, key, -1, -1, true);
                                            faceIdx[id1] = ch1;
                                        }
                                    }
                        		}
                            } else {
                            	key = trie.getNextKey(key * 26 + ch2);
                            	if (key > 0) {
                            		faceIdx[id1] = -1;
                    				if (trie.hasRadix(key)) {
                            			findWordsDFSCombo(id1, key, 
                                        		trie.getRadixInit(key), trie.getRadixLength(key) - 1, trie.hasTrie(key));
                            		} else {
                            			String word = trie.getWordRadix(key);
                            			if (word != null) {
                            				words.add(word);
                            			    //System.out.println("\t + word");
                                        }
                            			if (trie.hasTrie(key)) {
                            				findWordsDFSCombo(id1, key, -1, -1, true);                            				
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
                                findWordsDFSCombo(id1, key, 
                                		trie.getRadixInit(key), trie.getRadixLength(key) - 1, trie.hasTrie(key));
                            } else {
                                findWordsDFSCombo(id1, key, -1, -1, true);
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
    
    
    // recursive depth first search the Boggle board to find all new words and
    // add to the words set
    private void findWordsDFSCombo(int id, int key, int radixPos, int radixLength, boolean hasNextTrie) {
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
                	if (combo[id2]) {
                		int radixPos2 = trie.getRadixInit(nextKey);
                		int remainLength2 = trie.getRadixLength(nextKey) - 2;
                		if (comboIdx[id2] == trie.getNextCharIndex(radixPos2)) {
                			if (remainLength2> -1) {
                                faceIdx[id2] = -1;
                                findWordsDFSCombo(id2, key, radixPos2 + 1, remainLength2, trie.hasTrie(nextKey));
                                faceIdx[id2] = ch;
                            } else {
                            	String word = trie.getWordRadix(nextKey);
                                if (word != null) {
                                    words.add(word);
                                    //System.out.println("\t" + word);
                                }
                                if (trie.hasTrie(nextKey)) {
                                    faceIdx[id2] = -1;
                                    findWordsDFSCombo(id2, key, -1, -1, true);
                                    faceIdx[id2] = ch;
                                }
                            }
                		}
                	} else {
                		faceIdx[id2] = -1;
                        findWordsDFSCombo(id2, nextKey, 
                                trie.getRadixInit(nextKey), trie.getRadixLength(nextKey) - 1, trie.hasTrie(nextKey));
                        faceIdx[id2] = ch;
                	}
                } else {
                	if (combo[id2] && trie.hasTrie(nextKey)) {
                		nextKey = trie.getNextKey(nextKey * 26 + comboIdx[id2]);
                		if (trie.hasRadix(nextKey)) {
                            faceIdx[id2] = -1;
                            findWordsDFSCombo(id2, nextKey, 
                                    trie.getRadixInit(nextKey), trie.getRadixLength(nextKey) - 1, trie.hasTrie(nextKey));
                            faceIdx[id2] = ch;
                        } else {
                        	String word = trie.getWord(nextKey);
                            if (word != null) {
                                words.add(word);
                                //System.out.println("\t" + word);
                            }
                            if (nextKey > 0 && trie.hasTrie(nextKey)) {
                                faceIdx[id2] = -1;
                                findWordsDFSCombo(id2, nextKey, -1, -1, true);
                                faceIdx[id2] = ch;
                            }
                        }
                	} else if (!combo[id2]){
                		String word = trie.getWord(nextKey);
                        if (word != null) {
                            words.add(word);
                            //System.out.println("\t" + word);
                        }
                        if (trie.hasTrie(nextKey)) {
                            faceIdx[id2] = -1;
                            findWordsDFSCombo(id2, nextKey, -1, -1, true);
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
                	if (combo[id2]) {
                		if (remainLength > -1) {
                			if (comboIdx[id2] == trie.getNextCharIndex(radixPos + 1)) {
                				if (remainLength - 1 > -1) {
                					faceIdx[id2] = -1;
                                    findWordsDFSCombo(id2, key, radixPos + 2, remainLength - 1, hasNextTrie);
                                    faceIdx[id2] = ch;
                            	} else {
                            		String word = trie.getWordRadix(key);
                                    if (word != null) {
                                        words.add(word);
                                        //System.out.println("\t" + word);
                                    }
                                    if (hasNextTrie) {
                                    	faceIdx[id2] = -1;
                                        findWordsDFSCombo(id2, key, -1, -1, true);
                                        faceIdx[id2] = ch;
                                    }
                            	}
                			}
                        } else if (hasNextTrie) {
                            int nextKey = trie.getNextKey(key * 26 + comboIdx[id2]);
                            if (nextKey > 0) {
                            	if (trie.hasRadix(nextKey)) {
                                    faceIdx[id2] = -1;
                                    findWordsDFSCombo(id2, nextKey, 
                                            trie.getRadixInit(nextKey), trie.getRadixLength(nextKey) - 1, trie.hasTrie(nextKey));
                                    faceIdx[id2] = ch;
                                } else {
                                    String word = trie.getWord(nextKey);
                                    if (word != null) {
                                        words.add(word);
                                        //System.out.println("\t" + word);
                                    }
                                    if (trie.hasTrie(nextKey)) {
                                        faceIdx[id2] = -1;
                                        findWordsDFSCombo(id2, nextKey, -1, -1, true);
                                        faceIdx[id2] = ch;
                                    }
                                }
                            }
                        }
                	} else {
                		if (remainLength > -1) {
                            faceIdx[id2] = -1;
                            findWordsDFSCombo(id2, key, radixPos + 1, remainLength, hasNextTrie);
                            faceIdx[id2] = ch;
                        } else {
                            String word = trie.getWordRadix(key);
                            if (word != null) {
                                words.add(word);
                                //System.out.println("\t" + word);
                            }
                            if (hasNextTrie) {
                                faceIdx[id2] = -1;
                                findWordsDFSCombo(id2, key, -1, -1, true);
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
    
    public int scoreOfBig(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word is mandatory");
        }
        if (trie.contains(word)) {
            switch (word.length()) {
            case 3: 
                return 0;
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            case 8: 
                return 11;
            default:
            	return (word.length() - 8) * 2 + 11;
            }
        }
        return 0;
    }
    
    public static void main(String[] args) {
        BoggleDictionary dictionary = new BoggleDictionary(DictionaryOptions.OSPD);
        BoggleSolver solver = new BoggleSolver(dictionary);
    	
    	//BoggleBoard board = new BoggleBoard(args[0]);
        for (int i = 0; i < 10; i ++) {
        	BoggleBoard board = new BoggleBoard(BoggleOptions.BIG);
        	System.out.println(board);
        	int score = 0;
        	/*
        	for (String word : solver.getAllValidWords(board)) {
        		score += solver.scoreOf(word);
        		System.out.println(word + "\t" + solver.scoreOf(word));
        	}
        	System.out.println(score);
        	
        	score = 0;
        	*/
        	for (String word : solver.getAllValidWords(board)) {
        		int sc = solver.scoreOfBig(word);
        		score += solver.scoreOfBig(word);
        		if (sc > 0) 
        			System.out.println(word + "\t" + sc);
        	}
        	System.out.println(score);
        }
    }
}