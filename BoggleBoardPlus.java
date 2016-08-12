package mwong.myprojects.boggle;

import mwong.algs4.resources.boggle.BoggleBoard;

/****************************************************************************
 *  @author   Meisze Wong
 *            www.linkedin.com/pub/macy-wong/46/550/37b/
 *
 *  Compilation: javac BoggleBoardPlus26R.java
 *  Dependencies: BoggleBoard.java
 *
 *  A data type for BoardSolver, convert the BoggleBoard character to 
 *  trie character index and a set of it's neighbors positions
 *
 ****************************************************************************/
public class BoggleBoardPlus {
    private int[] faceIdx;
    private int[] nbrs;
    
    /**
     * Initializes Boggle Board cooperate with trie object and BoggleSolver
     */
    BoggleBoardPlus(BoggleBoard board, int offset) {    
        int rows = board.rows();
        int cols = board.cols();
        int size = rows * cols;
        faceIdx = new int[size];
        int idx = 0;       
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                faceIdx[idx++] = board.getLetter(row, col) - offset;
             }
        }
        if (rows > 1 && cols > 1) {
            nbrs = new int[size + 8 * cols * rows - 6 * cols - 6 * rows + 5];
            buildNeighbors(rows, cols, size);
        } else {
            nbrs = new int[size * 3 - 1];
            buildNeighborsLine(rows, cols, size);
        }
    }
    
    // generate a set of all dices neighbors position, all dices form in a line
    private void buildNeighborsLine(int rows, int cols, int size) {
        int idx = size + 1;
        int count = 0;
        nbrs[count++] = idx;
        nbrs[idx++] = 1;        
        if (cols == 1) {
            // dice with 2 neighbors
            for (int row = 1; row < rows - 1; row++) {
                nbrs[count++] = idx;
                nbrs[idx++] = row - 1;
                nbrs[idx++] = row + 1;
            }
            // tail of line
            nbrs[count++] = idx;
            nbrs[idx++] = rows - 2;
            nbrs[count] = idx;
        } else {
            // dice with 2 neighbors
            for (int col = 1; col < cols - 1; col++) {
                nbrs[count++] = idx;
                nbrs[idx++] = col - 1;
                nbrs[idx++] = col + 1;
            }
            // tail of line
            nbrs[count++] = idx;
            nbrs[idx++] = cols - 2;
            nbrs[count] = idx;
        }
    }
    
    // generate a set of all dices neighbors position, all dices form in M x N board
    private void buildNeighbors(int rows, int cols, int size) {
        int idx = size + 1;
        int count = 0;
        nbrs[count++] = idx;
        nbrs[idx++] = 1;
        nbrs[idx++] = cols;
        nbrs[idx++] = cols + 1;
        
        for (int col = 1; col < cols - 1; col++) {
            nbrs[count] = idx;
            nbrs[idx++] = count - 1;
            nbrs[idx++] = count + 1;
            nbrs[idx++] = count + cols - 1;
            nbrs[idx++] = count + cols;
            nbrs[idx++] = (count++) + cols + 1;
        }
        
        nbrs[count] = idx;
        nbrs[idx++] = count - 1;
        nbrs[idx++] = count + cols - 1;
        nbrs[idx++] = (count++) + cols;
        
        for (int row = 1; row < rows - 1; row++) {
            nbrs[count] = idx;
            nbrs[idx++] = count - cols;
            nbrs[idx++] = count - cols + 1;
            nbrs[idx++] = count + 1;
            nbrs[idx++] = count + cols;
            nbrs[idx++] = (count++) + cols + 1;
            
            for (int col = 1; col < cols - 1; col++) {
                nbrs[count] = idx;
                nbrs[idx++] = count - cols - 1;
                nbrs[idx++] = count - cols;
                nbrs[idx++] = count - cols + 1;
                nbrs[idx++] = count - 1;
                nbrs[idx++] = count + 1;
                nbrs[idx++] = count + cols - 1;
                nbrs[idx++] = count + cols;
                nbrs[idx++] = (count++) + cols + 1;
            }
            
            nbrs[count] = idx;
            nbrs[idx++] = count - cols - 1;
            nbrs[idx++] = count - cols;
            nbrs[idx++] = count - 1;
            nbrs[idx++] = count + cols - 1;
            nbrs[idx++] = (count++) + cols;
        }
        
        nbrs[count] = idx;
        nbrs[idx++] = count - cols;
        nbrs[idx++] = count - cols + 1;
        nbrs[idx++] = (count++) + 1;
        
        for (int col = 1; col < cols - 1; col++) {
            nbrs[count] = idx;
            nbrs[idx++] = count - cols - 1;
            nbrs[idx++] = count - cols;
            nbrs[idx++] = count - cols + 1;
            nbrs[idx++] = count - 1;
            nbrs[idx++] = (count++) + 1;
        }
        
        nbrs[count] = idx;
        nbrs[idx++] = count - cols - 1;
        nbrs[idx++] = count - cols;
        nbrs[idx++] = (count++) - 1;
        nbrs[count] = idx;
    }

    /**
     * Returns the integer array of BoggleBoard dice value in trie character index.
     * 
     * @return integer array of BoggleBoard dice value in trie character index
     */
    protected int[] getFaceIdx() {
        return faceIdx;
    }

    /**
     * Returns the integer array of BoggleBoard dice neighbors positions.
     * 
     * @return integer array of BoggleBoard dice neighbors positions
     */
    protected int[] getNbrs() {
        return nbrs;
    }
}
    
    
