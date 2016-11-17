package mwong.myprojects.boggle;

import java.util.Random;

/**
 * BoggleBoard is the data type of board for Boggle game.  It take the BoggleOpion
 * or a list of gui codes of dices' face.  It support double letters and blank letter.
 *
 * <p>Dependencies : BoggleOptions.java
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 */
public class BoggleBoard {
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String doubleLetter = "AEHIQT";
    private static final char blank = '#';

    private int size;        // number of columns
    private char[][] board;
    private boolean [][] isDouble;

    /**
     * Initializes a BoggleBoard object, generate a random Classic 4x4 board.
     */
    public BoggleBoard() {
        this(BoggleOptions.CLASSIC);
    }

    /**
     * Initializes a BoggleBoard object, generate a random board of given board option.
     *
     * @param option the given Boggle option
     */
    public BoggleBoard(BoggleOptions option) {
        Random random = new Random();
        size = option.getSize();
        String[] dices = option.getDices();
        int length = size * size;
        char[] charList = new char[length];
        boolean[] doubleList = new boolean[length];
        int count = 0;
        while (count < length) {
            int diceOrder = random.nextInt(count + 1);
            charList[count] = charList[diceOrder];
            doubleList[count] = doubleList[diceOrder];
            int randFace = random.nextInt(6);
            char letter = dices[count].charAt(randFace);
            charList[diceOrder] = letter;
            doubleList[diceOrder] = false;
            if (option.hasDoubleLetters(count)) {
                doubleList[diceOrder] = true;
            } else if (letter == 'Q') {
                doubleList[diceOrder] = true;
            }
            count++;
        }
        setProperties(charList, doubleList);
    }

    /**
     * Initializes a BoggleBoard object of a given list of gui codes.
     *
     * @param size the given Boggle size
     * @param codes the given byte list of gui codes of the board
     * @throws ex IllegalArgumentException
     */
    public BoggleBoard(int size, byte[] codes) {
        this.size = size;
        int length = size * size;
        if (codes.length != length) {
            throw new IllegalArgumentException();
        }

        char[] charList = new char[length];
        boolean[] doubleList = new boolean[length];
        int count = 0;
        for (int code : codes) {
            System.out.println(code);
            if (code == 0) {
                charList[count] = blank;
            } else if (code < 27) {
                charList[count] = alphabet.charAt(code - 1);
            } else if (code > 100 && code < 127) {
                charList[count] = alphabet.charAt(code - 101) ;
                if (doubleLetter.indexOf(charList[count]) == -1) {
                    throw new IllegalArgumentException();
                }
                doubleList[count] = true;
            } else {
                throw new IllegalArgumentException();
            }
            count++;
        }
        setProperties(charList, doubleList);
    }

    /**
     * Returns the character of blank face.
     *
     * @return character of blank face
     */
    public static final char getBlank() {
        return blank;
    }

    // set the properties in 2 dimensions arrays.
    private void setProperties(char[] charList, boolean[] doubleDice) {
        board = new char[size][size];
        isDouble = new boolean[size][size];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = charList[idx];
                isDouble[i][j] = doubleDice[idx++];
            }
        }
    }

    /**
     * Returns the number of Boggle size.
     *
     * @return number of Boggle size
     */
    public int getSize() {
        return size;
    }

    /**
     * Returns the first letter of dice's face in row i and column j.
     *
     * @param row the row
     * @param col the column
     * @return first letter in row i and column j
     */
    public char getFirstLetter(int row, int col) {
        return board[row][col];
    }

    /**
     * Returns the boolean of dice face is a double letter in row i and column j.
     *
     * @param row the row
     * @param col the column
     * @return boolean of dice face is a double letter in row i and column j
     */
    public boolean isDoubleLetter(int row, int col) {
        return isDouble[row][col];
    }

    /**
     * Returns the character in lowercase of the second letter.
     *
     * @param ch the character of the first letter
     * @return character in lowercase of the second letter
     */
    public char get2ndLower(char ch) {
        return (new String("" + get2ndUpper(ch))).toLowerCase().charAt(0);
    }

    /**
     * Returns the character in uppercase of the second letter.
     *
     * @param ch the character of the first letter
     * @return character in uppercase of the second letter
     */
    public char get2ndUpper(char ch) {
        switch (ch) {
            case 'A': return 'N';
            case 'E': return 'R';
            case 'H': return 'E';
            case 'I': return 'N';
            case 'Q': return 'U';
            case 'T': return 'H';
            default: return '\0';
        }
    }

    /**
     * Returns a string representation of the board includes blank and double letters.
     *
     * @return a string representation of the board includes blank and double letters
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(board[i][j]);
                if (isDouble[i][j]) {
                    sb.append(get2ndLower(board[i][j]) + " ");
                } else {
                    sb.append("  ");
                }
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Returns an integer array representation of the gui codes of the board.
     *
     * @return an integer array representation of the gui codes of the board
     */
    public int[] guiCode() {
        int[] list = new int[size * size];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char ch = board[i][j];
                if (ch != blank) {
                    list[idx] = ch - 'A' + 1;
                    if (isDouble[i][j] && ch != 'Q') {
                        list[idx] += 100;
                    }
                }
                idx++;
            }
        }
        return list;
    }
}
