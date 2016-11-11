package mwong.myprojects.boggle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class BoggleBoard {
	private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String doubleLetter = "AEHIQT";
	private static final char blank = '#';
    
	private int row;        // number of rows
    private int col;        // number of columns
    private char[][] board;
	private boolean [][] combo;
	
    public BoggleBoard() {
    	this(BoggleOptions.CLASSIC);
    }
    
    public BoggleBoard(BoggleOptions option) {
    	Random random = new Random();
    	int size = option.getSize();
        String[] dices = option.getDices();
        int length = size * size;
        char[] charList = new char[length];
        boolean[] doubleList = new boolean[length];
        int count = 0;
        while (count < length) {
            int rand = random.nextInt(count + 1);
            charList[count] = charList[rand];
            doubleList[count] = doubleList[rand];
            int rand2 = random.nextInt(6);
        	char letter = dices[count].charAt(rand2);
            charList[rand] = letter;
            doubleList[rand] = false;
            if (option.hasDoubleLetters(count)) {
            	doubleList[rand] = true; 
            } else if (letter == 'Q') {
        		doubleList[rand] = true; 
        	} 
            count++;
        }
        setProperties(size, size, charList, doubleList);
    }
    
    public BoggleBoard(int size, byte[] codes) {
    	int length = size * size;
    	if (codes.length != length)
    		throw new IllegalArgumentException();
    	
        char[] charList = new char[length];
        boolean[] doubleList = new boolean[length];
        int count = 0;
        for (int code : codes) {
            if (code == 0) {
            	charList[count] = blank;
            } else if (code < 27) {
            	charList[count] = alphabet.charAt(code - 1);
            } else {
            	charList[count] = alphabet.charAt(code - 101);
            	doubleList[count] = true;
            }
            count++;
        }
        setProperties(size, size, charList, doubleList);
    }
    
    public BoggleBoard(int size, char[] charList) {
    	validateCharList(size, charList, null);
    	boolean[] doubleList = new boolean[charList.length];
    	for (int i = 0; i < charList.length; i++) {
    		if (charList[i] == 'Q') {
    			doubleList[i] = true;
    		}
    	}
    	setProperties(size, size, charList, doubleList);
    }
    
    public BoggleBoard(int size, char[] charList, boolean[] doubleDice) {
    	validateCharList(size, charList, doubleDice);
    	setProperties(size, size, charList, doubleDice);
    } 
    /**
     * Initializes a board from the given filename.
     * @param filename the name of the file containing the Boggle board
     */
    public BoggleBoard(String filename) {
    	try (Scanner scanner = new Scanner(new File(filename));) {
        	int row = scanner.nextInt();
            int col = scanner.nextInt();
            int length = row * col;
            char[] charList = new char[length];
            boolean[] doubleList = new boolean[length];
            
            for (int i = 0; i < length; i++) {
            	String str = scanner.next().toUpperCase();
            	char letter1 = str.charAt(0);
            	if (str.equals("QU")) {
            		charList[i] = 'Q';
            		doubleList[i] = true;
            	} else if (str.length() != 1) {
            		throw new IllegalArgumentException("invalid character: " + str);
            	} else if (alphabet.indexOf(letter1) == -1) {
            		throw new IllegalArgumentException("invalid character: " + letter1);
            	} else {
            		charList[i] = letter1;
                }
            }
            setProperties(row, col, charList, doubleList);
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
    }

    
    public static final char getBlank() {
		return blank;
	}

	private void validateCharList(int size, char[] charList, boolean[] doubleDice) {
    	if (charList.length != size * size) {
    		throw new IllegalArgumentException("size and character list not matched");
    	}
    	for (int i = 0; i < charList.length; i++) {
    		if (alphabet.indexOf(charList[i]) == -1) {
    			throw new IllegalArgumentException("Invalid input: " + charList.toString());
    		}
    	}
    	if (doubleDice == null)
    		return;
    	for (int i = 0; i < doubleDice.length; i++) {
    		if (doubleDice[i] && doubleLetter.indexOf(charList[i]) == -1) {
    			throw new IllegalArgumentException("Invalid complex letter: " + charList[i]);
    		}
    	}
    }
    
    private void setProperties(int row, int col, char[] charList, boolean[] doubleDice) {       
        this.row = row;
        this.col = col;
        board = new char[row][col];
        combo = new boolean[row][col];
        int idx = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                board[i][j] = charList[idx];
                combo[i][j] = doubleDice[idx++];
            }
        }
    }
    
    /**
     * Returns the number of rows.
     * @return number of rows
     */
    public int rows() { return row; }

    /**
     * Returns the number of columns.
     * @return number of columns
     */
    public int cols() { return col; }

    /**
     * Returns the letter in row i and column j,
     * with 'Q' representing the two-letter sequence "Qu".
     * @param i the row
     * @param j the column
     * @return the letter in row i and column j
     *    with 'Q' representing the two-letter sequence "Qu".
     */
    public char getLetter(int i, int j) {
        return board[i][j];
    }
    
    public boolean hasCombo(int i, int j) {
        return combo[i][j];
    }
    
    public char get2ndLower(char ch) {
    	switch(ch) {
    	case 'A': return 'n';
    	case 'E': return 'r';
    	case 'H': return 'e';
    	case 'I': return 'n';
    	case 'Q': return 'u';
    	case 'T': return 'h';
    	default: return '\0';
    	}
    }

    public char get2ndUpper(char ch) {
    	switch(ch) {
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
     * Returns a string representation of the board, replacing 'Q' with "Qu".
     * @return a string representation of the board, replacing 'Q' with "Qu"
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(row + " " + col + "\n");
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                sb.append(board[i][j]);
                if (combo[i][j]) 
                	sb.append(get2ndLower(board[i][j]) + " ");
                else sb.append("  ");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    public int[] guiCode() {
    	int[] list = new int[row * col];
    	int idx = 0;
    	for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
            	char ch = board[i][j];
            	if (ch != blank) {
            		list[idx] = ch - 'A' + 1;
                    if (combo[i][j] && ch != 'Q') {
                    	list[idx] += 100;
                    }
                }
                idx++;
            }
        }
    	return list;
    }
}
