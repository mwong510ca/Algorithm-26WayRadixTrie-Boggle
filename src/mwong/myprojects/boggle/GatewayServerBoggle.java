package mwong.myprojects.boggle;

import java.util.HashSet;
import java.util.Set;

import py4j.GatewayServer;

/**
 * GatewayServerFifteenPuzzle for pyqt5 GUI front end to connect to 15 puzzle solvers.
 * It use standalone reference collection.
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 */
public class GatewayServerBoggle {
	BoggleSolver solver;
	BoggleBoard board;
	BoggleDictionary dictionary;
	Set<String> words;
	String wordsList;
	int maxScores;
	String dictionaryFilepath, inUseDictionary;
	 
    public GatewayServerBoggle() {
    	dictionary = new BoggleDictionary();
    	solver = new BoggleSolver(dictionary);
    	setDictionaryOspd();
    	wordsList = "";
    	words = new HashSet<String>();
    	maxScores = 0;
    }
    
    public void setDictionaryOspd() {
    	if (inUseDictionary == DictionaryOptions.OSPD.getAcronym())
    		return;
    	dictionary = new BoggleDictionary(DictionaryOptions.OSPD);
    	inUseDictionary = DictionaryOptions.OSPD.getAcronym();
    	if (dictionary.isEmpty()) {
    		dictionary = new BoggleDictionary();
    		inUseDictionary = "default";
    	}
    	solver.setDictionary(dictionary);
    	if (board != null) {
    		int size = board.rows();
    		if (size < 6) {
    			setAllWords(3);
    		} else {
    			setAllWords(4);
    		}
    	}
    }
    
    public void setDictionaryEowl() {
    	if (inUseDictionary == DictionaryOptions.EOWL.getAcronym())
    		return;
    	dictionary = new BoggleDictionary(DictionaryOptions.EOWL);
    	inUseDictionary = DictionaryOptions.EOWL.getAcronym();
    	if (dictionary.isEmpty()) {
    		dictionary = new BoggleDictionary();
    		inUseDictionary = "default";
    	}
    	solver.setDictionary(dictionary);
    	if (board != null) {
    		int size = board.rows();
    		if (size < 6) {
    			setAllWords(3);
    		} else {
    			setAllWords(4);
    		}
    	}
    }
    
    public void setDictionarySowpods() {
    	if (inUseDictionary == DictionaryOptions.SOWPODS.getAcronym())
    		return;
    	dictionary = new BoggleDictionary(DictionaryOptions.SOWPODS);
    	inUseDictionary = DictionaryOptions.SOWPODS.getAcronym();
    	if (dictionary.isEmpty()) {
    		dictionary = new BoggleDictionary();
    		inUseDictionary = "default";
    	}
    	solver.setDictionary(dictionary);
    	if (board != null) {
    		int size = board.rows();
    		if (size < 6) {
    			setAllWords(3);
    		} else {
    			setAllWords(4);
    		}
    	}
    }
    
    public void setDictionaryCustom(String filename) {
    	dictionary = new BoggleDictionary(filename);
    	inUseDictionary = "custom";
    	if (dictionary.isEmpty()) {
    		dictionary = new BoggleDictionary();
    		inUseDictionary = "default";
    	}
    	solver.setDictionary(dictionary);
    	if (board != null) {
    		int size = board.rows();
    		if (size < 6) {
    			setAllWords(3);
    		} else {
    			setAllWords(4);
    		}
    	}
    }
    
    public BoggleBoard getClassicBoard() {
        board = new BoggleBoard(BoggleOptions.CLASSIC);
        setAllWords(BoggleOptions.CLASSIC.getMinWordLength());
        return board;
    }

    public BoggleBoard getNew1992Board() {
        board = new BoggleBoard(BoggleOptions.NEW1992);
        setAllWords(BoggleOptions.NEW1992.getMinWordLength());
        return board;
    }

    public BoggleBoard getDeluxeBoard() {
        board = new BoggleBoard(BoggleOptions.DELUXE);
        setAllWords(BoggleOptions.DELUXE.getMinWordLength());
        return board;
    }

    public BoggleBoard getBigBoard() {
        board = new BoggleBoard(BoggleOptions.BIG);
        setAllWords(BoggleOptions.BIG.getMinWordLength());
        return board;
    }

    public BoggleBoard getSuperBigBoard() {
        board = new BoggleBoard(BoggleOptions.SUPERBIG);
        setAllWords(BoggleOptions.SUPERBIG.getMinWordLength());
        return board;
    }
    
    public BoggleBoard getCustomBoard(int size, byte[] codes) {
        board = new BoggleBoard(size, codes);
        if (size < 6) {
        	setAllWords(3);
        } else {
        	setAllWords(4);
        }
        return board;
    }
    
    public void setAllWords(int minWordLength) {
    	wordsList = "";
    	words = new HashSet<String>();
    	maxScores = 0;
    	if (board == null)
    		return;
    	Iterable<String> list = solver.getAllValidWords(board);
    	for (String word : list) {
    		if (word.length() >= minWordLength) {
    			words.add(word);
    			wordsList += word + "\n";
    		
    			if (minWordLength == 3) {
        			maxScores += solver.scoreOf(word);
        		} else {
        			maxScores += solver.scoreOfBig(word);
        		}
    		}
    	}
    }
    
    public Set<String> getBoggleWords() {	
    	return words;
    }
    
    public int getMaxScores() {
    	return maxScores;
    }
    
    public String getWordsList() {
    	return wordsList.trim();
    }
    
    public final String getInUseDictionary() {
		return inUseDictionary;
	}

	public final String getDictionaryFilepath() {
		return dictionaryFilepath;
	}

	/**
     * Main application to start the gateway server.
     * @param args standard argument main function
     */
    public static void main(String[] args) {
    	int port = Integer.parseUnsignedInt(args[0]);
    	if (port < 25335 || port > 65535) {
    		throw new IllegalArgumentException("invalid port : " + port);
    	}
    	GatewayServer gatewayServer = new GatewayServer(new GatewayServerBoggle(), port);
        gatewayServer.start();
        System.out.println("Gateway server for boggle started using port " + port);
    }
}