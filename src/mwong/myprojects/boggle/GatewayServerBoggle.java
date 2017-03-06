package mwong.myprojects.boggle;

import py4j.GatewayServer;

import java.util.HashSet;
import java.util.Set;

/**
 * GatewayServerBoggle for pyqt5 GUI front end to connect to boggle solvers.
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 *         www.github.com/mwong510ca/Boggle_TrieDataStructure
 */

public class GatewayServerBoggle {
    BoggleSolver solver;
    BoggleBoard board;
    BoggleDictionary dictionary;
    Set<String> words;
    String wordsList;
    int maxScores;
    String dictionaryFilepath;
    String inUseDictionary;

    /**
     * Initialize GatewayServerBoggle with default dictionary.
     */
    public GatewayServerBoggle() {
        dictionary = new BoggleDictionary();
        solver = new BoggleSolver(dictionary);
        wordsList = "";
        words = new HashSet<String>();
        maxScores = 0;
    }

    /**
     * Change the BoggleDictionary to OSPD.
     */
    public void setDictionaryOspd() {
        if (inUseDictionary == DictionaryOptions.OSPD.getAcronym()) {
            return;
        }
        dictionary = new BoggleDictionary(DictionaryOptions.OSPD);
        inUseDictionary = DictionaryOptions.OSPD.getAcronym();
        if (dictionary.isEmpty()) {
            dictionary = new BoggleDictionary();
            inUseDictionary = "default";
        }
        solver.setDictionary(dictionary);
        if (board != null) {
            int size = board.getSize();
            if (size < 6) {
                setAllWords(3);
            } else {
                setAllWords(4);
            }
        }
    }

    /**
     * Change the BoggleDictionary to EOWL.
     */
    public void setDictionaryEowl() {
        if (inUseDictionary == DictionaryOptions.EOWL.getAcronym()) {
            return;
        }
        dictionary = new BoggleDictionary(DictionaryOptions.EOWL);
        inUseDictionary = DictionaryOptions.EOWL.getAcronym();
        if (dictionary.isEmpty()) {
            dictionary = new BoggleDictionary();
            inUseDictionary = "default";
        }
        solver.setDictionary(dictionary);
        if (board != null) {
            int size = board.getSize();
            if (size < 6) {
                setAllWords(3);
            } else {
                setAllWords(4);
            }
        }
    }

    /**
     * Change the BoggleDictionary to SOWPODS.
     */
    public void setDictionarySowpods() {
        if (inUseDictionary == DictionaryOptions.SOWPODS.getAcronym()) {
            return;
        }
        dictionary = new BoggleDictionary(DictionaryOptions.SOWPODS);
        inUseDictionary = DictionaryOptions.SOWPODS.getAcronym();
        if (dictionary.isEmpty()) {
            dictionary = new BoggleDictionary();
            inUseDictionary = "default";
        }
        solver.setDictionary(dictionary);
        if (board != null) {
            int size = board.getSize();
            if (size < 6) {
                setAllWords(3);
            } else {
                setAllWords(4);
            }
        }
    }

    /**
     * Change the BoggleDictionary to the given dictionary file.
     */
    public void setDictionaryCustom(String filename) {
        dictionary = new BoggleDictionary(filename);
        inUseDictionary = "custom";
        if (dictionary.isEmpty()) {
            dictionary = new BoggleDictionary();
            inUseDictionary = "default";
        }
        solver.setDictionary(dictionary);
        if (board != null) {
            int size = board.getSize();
            if (size < 6) {
                setAllWords(3);
            } else {
                setAllWords(4);
            }
        }
    }

    /**
     * Return a random 4x4 classic Boggle Board and store a list
     * of all valid words.
     *
     *  @return BoggleBoard object of 4x4 Classic Boggle board
     */
    public BoggleBoard getClassicBoard() {
        board = new BoggleBoard(BoggleOptions.CLASSIC);
        setAllWords(BoggleOptions.CLASSIC.getMinWordLength());
        return board;
    }

    /**
     * Return a random 4x4 new 1992 version Boggle Board and store a list
     * of all valid words.
     *
     *  @return BoggleBoard object of 4x4 Classic Boggle board
     */
    public BoggleBoard getNew1992Board() {
        board = new BoggleBoard(BoggleOptions.NEW1992);
        setAllWords(BoggleOptions.NEW1992.getMinWordLength());
        return board;
    }

    /**
     * Return a random 5x5 deluxe Boggle Board and store a list
     * of all vaild words.
     *
     *  @return BoggleBoard object of 4x4 Classic Boggle board
     */
    public BoggleBoard getDeluxeBoard() {
        board = new BoggleBoard(BoggleOptions.DELUXE);
        setAllWords(BoggleOptions.DELUXE.getMinWordLength());
        return board;
    }

    /**
     * Return a random 5x5 big Boggle Board and store a list
     * of all valid words.
     *
     *  @return BoggleBoard object of 4x4 Classic Boggle board
     */
    public BoggleBoard getBigBoard() {
        board = new BoggleBoard(BoggleOptions.BIG);
        setAllWords(BoggleOptions.BIG.getMinWordLength());
        return board;
    }

    /**
     * Return a random 6x6 super big Boggle Board and store a list
     * of all valid words.
     *
     *  @return BoggleBoard object of 4x4 Classic Boggle board
     */
    public BoggleBoard getSuperBigBoard() {
        board = new BoggleBoard(BoggleOptions.SUPERBIG);
        setAllWords(BoggleOptions.SUPERBIG.getMinWordLength());
        return board;
    }

    /**
     * Return a custom Boggle Board of the given size and a list
     * of gui codes and store a list of all valid words.
     *
     *  @return BoggleBoard object of 4x4 Classic Boggle board
     */
    public BoggleBoard getCustomBoard(int size, byte[] codes) {
        board = new BoggleBoard(size, codes);
        if (size < 6) {
            setAllWords(3);
        } else {
            setAllWords(4);
        }
        return board;
    }

    // scan the boggle board, store a set of all words and maximum scores
    private void setAllWords(int minWordLength) {
        wordsList = "";
        words = new HashSet<String>();
        maxScores = 0;
        if (board == null) {
            return;
        }

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

    /**
     * Return a set of all vaild words of the boggle board.
     *
     *  @return set of all vaild words of the boggle board
     */
    public final Set<String> getBoggleWords() {
        return words;
    }

    /**
     * Return an integer of maximum scores of the boggle board.
     *
     *  @return integer of maximum scores of the boggle board
     */
    public final int getMaxScores() {
        return maxScores;
    }

    /**
     * Return the String of all valid words, one word per line.
     *
     *  @return String of all valid words, one word per line
     */
    public final String getWordsList() {
        return wordsList.trim();
    }

    /**
     * Return the String of Dictionary Acronym currently using.
     *
     *  @return String of Dictionary Acronym currently using
     */
    public final String getInUseDictionary() {
        return inUseDictionary;
    }

    /**
     * Main application to start the gateway server with random port.
     *
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