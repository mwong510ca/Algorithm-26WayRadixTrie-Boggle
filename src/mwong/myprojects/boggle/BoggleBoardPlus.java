package mwong.myprojects.boggle;

/**
 * BoggleBoardPlus is the data type for BoggleSolver.  It convert the BoggleBoard
 * into trie character index and a set of it's neighbors positions.
 *
 * <p>Dependencies : BoggleBoard.java
 *
 * @author Meisze Wong
 *         www.linkedin.com/pub/macy-wong/46/550/37b/
 */
public class BoggleBoardPlus {
    private char blank = BoggleBoard.getBlank();
    private int[] faceIdx;
    private int[] nbrs;
    private boolean[] doubleLetter;
    private int[] letter2Idx;

    /**
     * Initializes Boggle Board cooperate with trie object and BoggleSolver.
     */
    BoggleBoardPlus(BoggleBoard board, int offset) {
        int rows = board.getSize();
        int cols = board.getSize();
        int size = rows * cols;
        faceIdx = new int[size];
        doubleLetter = new boolean[size];
        letter2Idx = new int[size];
        int idx = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                letter2Idx[idx] = -1;
                if (board.getFirstLetter(row, col) == blank) {
                    faceIdx[idx] = -1;
                } else {
                    char letter = board.getFirstLetter(row, col);
                    faceIdx[idx] = letter - offset;
                    if (board.isDoubleLetter(row, col) && letter != 'Q') {
                        doubleLetter[idx] = true;
                        letter2Idx[idx] = board.get2ndUpper(letter) - offset;
                    }
                }
                idx++;
            }
        }

        nbrs = new int[size + 8 * cols * rows - 6 * cols - 6 * rows + 5];
        buildNeighbors(rows, cols, size);
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
    final int[] getFaceIdx() {
        return faceIdx;
    }

    /**
     * Returns the integer array of BoggleBoard dice neighbors positions.
     *
     * @return integer array of BoggleBoard dice neighbors positions
     */
    final int[] getNbrs() {
        return nbrs;
    }

    /**
     * Returns the boolean array represents double letter of the dices.
     *
     * @return boolean array represents double letter of the dices
     */
    final boolean[] isDouble() {
        return doubleLetter;
    }

    /**
     * Returns the integer array of second letter index of the dices.
     *
     * @return integer array of second letter index of the dices
     */
    final int[] getDoubleIdx() {
        return letter2Idx;
    }
}

