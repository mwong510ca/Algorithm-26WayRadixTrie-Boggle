package mwong.myprojects.boggle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.TreeSet;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac BoggleDictionary.java
 *  Dependences: BoggleTrie26WayRadix.java
 *
 *  A data type to load the dictionary into 26-way radix trie, reorder in specific
 *  order design for Boggle to improve performance
 *
 ****************************************************************************/

public class BoggleDictionary {
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final int OFFSET = BoggleTrie26WayRadix.getOffset();
    private static final int IDX_Q = BoggleTrie26WayRadix.getIdxQ();
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static String directory = "dictionary";
    
    private String filepath;
    private byte[] radix, radixLength;
    private int[] radixIdx, trieR26;
    private String[] words;
    private int[] visited;
    private boolean empty;
    private int hasWordGroup1, endTrie, hasWordGroup2, hasRadix, endRadix, marker;
    
    /**
     *  default constructor.
     */
    public BoggleDictionary() { 
        empty = true;
        marker = -1;
        filepath = "";
        String[] dict = (new BoggleDictionaryDefault()).getWord();
        loadDictionary(dict);
    }
    
    public BoggleDictionary(DictionaryOptions option) {
    	empty = true;
        marker = -1;
        filepath = directory + SEPARATOR + option.getFilename();
        String[] dict = readDictionary(filepath);
        loadDictionary(dict);
    }
    
    public BoggleDictionary(String filepath) {
    	empty = true;
        marker = -1;
        this.filepath = filepath;
        String[] dict = readDictionary(filepath);
        loadDictionary(dict);
    }
    
    void loadDataFile(String filename) throws FileNotFoundException, IOException {
    	String seperator = System.getProperty("file.separator");
        String directory = "database";
        String filepath = directory + seperator + filename;
        
        try (FileInputStream fin = new FileInputStream(filepath);
                FileChannel inChannel = fin.getChannel();) {
            ByteBuffer buf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            hasWordGroup1 = buf.getInt();
            endTrie = buf.getInt();  
            hasWordGroup2 = buf.getInt();  
            hasRadix = buf.getInt();  
            endRadix = buf.getInt();  

            int len = buf.getInt();
            radix = new byte[len];
            buf.get(radix);
            len = buf.getInt();
            radixLength = new byte[len];
            buf.get(radixLength);
            
            len = buf.getInt();
            radixIdx = new int[len];
            for (int idx = 0; idx < len;idx++) {
            	radixIdx[idx] = buf.getInt();
            }
            len = buf.getInt();
            trieR26 = new int[len];
            for (int idx = 0; idx < len;idx++) {
            	trieR26[idx] = buf.getInt();
            }
            
            len = buf.getInt();
            visited = new int[len];
            words = new String[len];
            for (int idx = 0; idx < len;idx++) {
            	byte size = buf.get();
            	if (size > 0) {
            		byte[] bytes = new byte[size];
            		buf.get(bytes);
                    words[idx] = new String(bytes);
                }
            }
            empty = false;
        }
    }
    
    void saveDataFile(String filename) {
    	if (empty) {
    		System.out.println("empty????");
    		return;
    	}
    	
    	String seperator = System.getProperty("file.separator");
        String directory = "database";
        String filepath = directory + seperator + filename;
        if (!(new File(directory)).exists()) {
            (new File(directory)).mkdir();
        }
        if (!(new File(filepath)).exists()) {
            (new File(filepath)).delete();
        }
        
        try (FileOutputStream fout = new FileOutputStream(filepath);
                FileChannel outChannel = fout.getChannel();) {
        	ByteBuffer buffer = ByteBuffer.allocateDirect(20);
            buffer.putInt(hasWordGroup1);
            buffer.putInt(endTrie);
            buffer.putInt(hasWordGroup2);
            buffer.putInt(hasRadix);
            buffer.putInt(endRadix);
            buffer.flip();
            outChannel.write(buffer);
            
            buffer = ByteBuffer.allocateDirect(radix.length + 4);
            buffer.putInt(radix.length);
            buffer.put(radix);
            buffer.flip();
            outChannel.write(buffer);
            
            buffer = ByteBuffer.allocateDirect(radixLength.length + 4);
            buffer.putInt(radixLength.length);
            buffer.put(radixLength);
            buffer.flip();
            outChannel.write(buffer);
            
            buffer = ByteBuffer.allocateDirect(radixIdx.length * 4 + 4);
            buffer.putInt(radixIdx.length);
            for (int idx : radixIdx)
            	buffer.putInt(idx);
            buffer.flip();
            outChannel.write(buffer);
            
            buffer = ByteBuffer.allocateDirect(trieR26.length * 4 + 4);
            buffer.putInt(trieR26.length);
            for (int idx : trieR26)
            	buffer.putInt(idx);
            buffer.flip();
            outChannel.write(buffer);
            
            buffer = ByteBuffer.allocateDirect(4);
            buffer.putInt(words.length);
            buffer.flip();
            outChannel.write(buffer);
            
            for (String str : words) {
            	if (str == null) {
            		buffer = ByteBuffer.allocateDirect(1);
            		buffer.put((byte) 0);
            	} else {
            		buffer = ByteBuffer.allocateDirect(str.length() + 1);
            		buffer.put((byte) str.length());
            		buffer.put(str.getBytes());
            	}
            	buffer.flip();
                outChannel.write(buffer);
            }
        } catch (BufferUnderflowException | IOException ex) {
            System.out.println("PatternDatabase - save data set in file failed");
            if ((new File(filepath)).exists()) {
                (new File(filepath)).delete();
            }
        }
    }
    
    private String[] readDictionary(String filepath) {
    	File file = new File(filepath);
    	if (!file.exists()) {
    		System.out.println(filepath + " not found."); 
			return null;
    	}
    	
    	TreeSet<String> lines = new TreeSet<String>();
    	try(BufferedReader buf = new BufferedReader(new FileReader(filepath))) {
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
        } catch (IOException e) {
			e.printStackTrace();
			return null;
		}
        String[] dict = new String[lines.size()];
        int idx = 0;
    	for (String str : lines)
    		dict[idx++] = str;
    	return dict;
    }
    
    /**
     *  Load the dictionary in trie object, retrieve trie components
     *  and reorder it and set a flag of each group of words.  
     *  
     *  @param dict string array of the dictionary
     */
    private void loadDictionary(String[] dict) {
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
        byte[] tempRadix = trie.getRadix();
        byte[] tempRadixLength = trie.getRadixLength();
        int[] tempRadixPos = trie.getRadixPos();
        String[] tempWords  = trie.getValues();
        int trieCounter = trie.getTrieCounter();
        int tempRadixIdx = trie.getRadixSize();

        // count the entries as describe below:
        //          Has next trie   is a word   has radix       flags
        // group1      Yes            No          No            hasWord1
        // group2      Yes            Yes         No            hasRadix
        // group3      Yes            No          Yes           hasWord2
        // group4      Yes            Yes         Yes           endTrie
        // group5      No             Yes         Yes           endRadix
        // remaining   No             Yes         No
        int group1 = 0, group2 = 0, group3 = 0, group4 = 0, group5 = 0; 
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
        radix = new byte[tempRadixIdx];
        System.arraycopy(tempRadix, 0, radix, 0, tempRadixIdx);
        radixIdx = new int[reset6 - reset3 + 1];
        radixLength = new byte[reset6 - reset3 + 1];
        
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

    protected final String getFilepath() {
		return filepath;
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
        int d = 0;
        int len = key.length();
        
        int trieIdx = key.charAt(d++) - OFFSET;
        if (trieIdx == IDX_Q) {
            if (key.charAt(d++) != 'U') {
                return false;
            }
        }
        int chIdx = key.charAt(d++) - OFFSET;
        trieIdx = trieR26[trieIdx * 26 + chIdx];
        
        while (trieIdx > 0) {
            if (chIdx == IDX_Q) {
                if (!(d < len && key.charAt(d++) == 'U')) {
                    return false;
                }
            }
            
            if (trieIdx > hasRadix && trieIdx < endRadix) {
                // Notes: radix storage store the ASCII code
            	int pos = radixIdx[trieIdx - hasRadix];
            	int radixLen = radixLength[trieIdx - hasRadix] - 1;
                do {
                    if (d >= len) {
                        return false;
                    }
                    int ascii = key.charAt(d++);
                    if (ascii != radix[pos++]) {
                        return false;
                    }
                    if (ascii == 'Q') {
                        if (!(d < len && key.charAt(d++) == 'U')) {
                            return false;
                        }
                    }
                } while (--radixLen > -1);
                if (d == len) {
                    return trieIdx > hasWordGroup2;
                }
            } else {
                if (d == len) {
                    return trieIdx > hasWordGroup1;
                }
            }
            
            // continue search
            if (trieIdx < endTrie) {
                chIdx = key.charAt(d++) - OFFSET;
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
     *  @param key the number of current key 
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
