package mwong.myprojects.boggle;

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
	private char blank = BoggleBoard.getBlank();
    private int[] faceIdx;
    private int[] nbrs;
    private boolean[] combo; 
    private int[] comboIdx; 
    
    /**
     * Initializes Boggle Board cooperate with trie object and BoggleSolver
     */
    BoggleBoardPlus(BoggleBoard board, int offset) {    
        int rows = board.rows();
        int cols = board.cols();
        int size = rows * cols;
        faceIdx = new int[size];
        combo = new boolean[size];
        comboIdx = new int[size];
        int idx = 0;       
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
            	comboIdx[idx] = -1;
            	if (board.getLetter(row, col) == blank) {
            		faceIdx[idx] = -1;
            	} else {
            		char letter = board.getLetter(row, col);
            		faceIdx[idx] = letter - offset;
            		if (board.hasCombo(row, col) && letter != 'Q') {
            			combo[idx] = true;
            			comboIdx[idx] = board.get2ndUpper(letter) - offset;
            		}
            	}
            	idx++;
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
        nbrs[count] = idx;
        if (faceIdx[count] != -1) {
        	if (faceIdx[1] != -1) {
        		nbrs[idx++] = 1;       
        	}
        }
        if (cols == 1) {
            // dice with 2 neighbors
            for (int row = 1; row < rows - 1; row++) {
                nbrs[++count] = idx;
                if (faceIdx[count] != -1) {
                	if (faceIdx[row - 1] != -1) {
                        nbrs[idx++] = row - 1;
                    }
                    if (faceIdx[row + 1] != -1) {
                        nbrs[idx++] = row + 1;
                    }
                }
            }
            // tail of line
            nbrs[++count] = idx;
            if (faceIdx[rows - 2] != -1) {
            	if (faceIdx[rows - 2] != -1) {
            		nbrs[idx++] = rows - 2;
            	}
            }
            nbrs[++count] = idx;
        } else {
            // dice with 2 neighbors
            for (int col = 1; col < cols - 1; col++) {
                nbrs[++count] = idx;
                if (faceIdx[count] != 1) {
                    if (faceIdx[col - 1] != -1) {
                    	nbrs[idx++] = col - 1;
                    }
                    if (faceIdx[col + 1] != -1) {
                    	nbrs[idx++] = col + 1;
                    }
                }
            }
            // tail of line
            nbrs[++count] = idx;
            if (faceIdx[cols - 2] != -1) {
            	if (faceIdx[cols - 2] != -1) {
            		nbrs[idx++] = cols - 2;
            	}
            }
            nbrs[++count] = idx;
        }
    }
    
    // generate a set of all dices neighbors position, all dices form in M x N board
    private void buildNeighbors(int rows, int cols, int size) {
        int idx = size + 1;
        int count = 0;
        nbrs[count] = idx;
        if (faceIdx[count] != -1) {
        	if (faceIdx[count + 1] != -1) {
                nbrs[idx++] = count + 1;
            }
            if (faceIdx[count + cols] != -1) {
            	nbrs[idx++] = count + cols;
            }
            if (faceIdx[count + cols + 1] != -1) {
            	nbrs[idx++] = count + cols + 1;
            }
        }
        count++;
        
        for (int col = 1; col < cols - 1; col++) {
            nbrs[count] = idx;
            if (faceIdx[count] != -1) {
                if (faceIdx[count - 1] != -1) {
                    nbrs[idx++] = count - 1;
                }
                if (faceIdx[count + 1] != -1) {
                	nbrs[idx++] = count + 1;
                }
                if (faceIdx[count + cols - 1] != -1) {
                	nbrs[idx++] = count + cols - 1;
                }
                if (faceIdx[count + cols] != -1) {
                	nbrs[idx++] = count + cols;
                }
                if (faceIdx[count + cols + 1] != -1) {
                	nbrs[idx++] = count + cols + 1;
                }
            }
            count++;
        }
        
        nbrs[count] = idx;
        if (faceIdx[count] != -1) {
            if (faceIdx[count - 1] != -1) {
            	nbrs[idx++] = count - 1;
            }
            if (faceIdx[count + cols - 1] != -1) {
            	nbrs[idx++] = count + cols - 1;
            }
            if (faceIdx[count + cols] != -1) {
            	nbrs[idx++] = count + cols;
            }
        }
        count++;
        
        for (int row = 1; row < rows - 1; row++) {
            nbrs[count] = idx;
            if (faceIdx[count] != -1) {
                if (faceIdx[count - cols] != -1) {
                    nbrs[idx++] = count - cols;
                }
                if (faceIdx[count - cols + 1] != -1) {
                	nbrs[idx++] = count - cols + 1;
                }
                if (faceIdx[count + 1] != -1) {
                	nbrs[idx++] = count + 1;
                }
                if (faceIdx[count + cols] != -1) {
                	nbrs[idx++] = count + cols;
                }
                if (faceIdx[count + cols + 1] != -1) {
                	nbrs[idx++] = count + cols + 1;
                }
            }
            count++;
            
            for (int col = 1; col < cols - 1; col++) {
                nbrs[count] = idx;
                if (faceIdx[count] != -1) {
                    if (faceIdx[count - cols - 1] != -1) {
                        nbrs[idx++] = count - cols - 1;
                    }
                    if (faceIdx[count - cols] != -1) {
                    	nbrs[idx++] = count - cols;
                    }
                    if (faceIdx[count - cols + 1] != -1) {
                    	nbrs[idx++] = count - cols + 1;
                    }
                    if (faceIdx[count - 1] != -1) {
                    	nbrs[idx++] = count - 1;
                    }
                    if (faceIdx[count + 1] != -1) {
                    	nbrs[idx++] = count + 1;
                    }
                    if (faceIdx[count + cols - 1] != -1) {
                    	nbrs[idx++] = count + cols - 1;
                    }
                    if (faceIdx[count + cols] != -1) {
                    	nbrs[idx++] = count + cols;
                    }
                    if (faceIdx[count + cols + 1] != -1) {
                    	nbrs[idx++] = count + cols + 1;
                    }
                }
                count++;
            }
            
            nbrs[count] = idx;
            if (faceIdx[count] != -1) {
                if (faceIdx[count - cols - 1] != -1) {
                    nbrs[idx++] = count - cols - 1;
                }
                if (faceIdx[count - cols] != -1) {
                	nbrs[idx++] = count - cols;
                }
                if (faceIdx[count - 1] != -1) {
                	nbrs[idx++] = count - 1;
                }
                if (faceIdx[count + cols - 1] != -1) {
                	nbrs[idx++] = count + cols - 1;
                }
                if (faceIdx[count + cols] != -1) {
                	nbrs[idx++] = count + cols;
                }
            }
            count++;
        }
        
        nbrs[count] = idx;
        if (faceIdx[count] != -1) {
            if (faceIdx[count - cols] != -1) {
            	nbrs[idx++] = count - cols;
            }
            if (faceIdx[count - cols + 1] != -1) {
            	nbrs[idx++] = count - cols + 1;
            }
            if (faceIdx[count + 1] != -1) {
            	nbrs[idx++] = count + 1;
            }
        }
        count++;
        
        for (int col = 1; col < cols - 1; col++) {
            nbrs[count] = idx;
            if (faceIdx[count] != -1) {
                if (faceIdx[count - cols - 1] != -1) {
                    nbrs[idx++] = count - cols - 1;
                }
                if (faceIdx[count - cols] != -1) {
                	nbrs[idx++] = count - cols;
                }
                if (faceIdx[count - cols + 1] != -1) {
                	nbrs[idx++] = count - cols + 1;
                }
                if (faceIdx[count - 1] != -1) {
                	nbrs[idx++] = count - 1;
                }
                if (faceIdx[count + 1] != -1) {
                	nbrs[idx++] = count + 1;
                }
            }
            count++;
        }
        
        nbrs[count] = idx;
        if (faceIdx[count] != -1) {
            if (faceIdx[count - cols - 1] != -1) {
                nbrs[idx++] = count - cols - 1;
            }
            if (faceIdx[count - cols] != -1) {
            	nbrs[idx++] = count - cols;
            }
            if (faceIdx[count - 1] != -1) {
            	nbrs[idx++] = count - 1;
            }
        }
        nbrs[++count] = idx;
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

	protected final boolean[] getCombo() {
		return combo;
	}
	protected final int[] getComboIdx() {
		return comboIdx;
	}
}
    
    
