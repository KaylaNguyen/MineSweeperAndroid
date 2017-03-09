package hu.ait.minesweeperandroid.model;

import java.util.Random;

/**
 * Created by khue on 3/6/17.
 */

public class MineSweeperModel {

    private static MineSweeperModel instance = null;
    public final int rowNum = 5;
    public final int colNum = 6;
    private int bombNum = 3;

    private Field[][] model;

    private MineSweeperModel() {
        model = new Field[rowNum][colNum];
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                model[i][j] = new Field();
            }
        }
        placeBomb();
        computeFieldContent();
    }

    public static MineSweeperModel getInstance() {
        if (instance == null) {
            instance = new MineSweeperModel();
        }
        return instance;
    }

    private void placeBomb() {
        short counter = 0;
        while (counter < bombNum) {
            int randomRow = new Random(System.currentTimeMillis()).nextInt(rowNum);
            int randomCol = new Random(System.currentTimeMillis()).nextInt(colNum);
            if (!model[randomRow][randomCol].isMine()) {
                model[randomRow][randomCol].placeMine();
                counter += 1;
            }
        }
    }

    private void computeFieldContent() {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                model[i][j].setNumMines(getAdjMines(i, j));
            }
        }
    }

    private short getAdjMines(int x, int y) {
        if (model[x][y].isMine())
            return -1;

        short countMines = 0;
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((!model[x][y].isMine()) && (inBound(i, j)) && (model[i][j].isMine())) {
                    countMines++;
                }
            }
        }
        return countMines;
    }

    private boolean inBound(int x, int y) {
        return !(((x < 0) || (x >= rowNum)) || ((y < 0) || (y >= colNum)));

    }

    public void resetModel() {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                model[i][j] = new Field();
            }
        }
        placeBomb();
        computeFieldContent();
    }

    public void flagField(int x, int y) {
        model[x][y].flag();
    }

    public void makeMove(int x, int y) {
        if (model[x][y].getNumAdjMines() == 0) {
            revealFieldRecursive(x, y);
        }
        model[x][y].reveal();
    }

    private void revealFieldRecursive(int x, int y) {
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if ((inBound(i, j)) && (!model[i][j].isRevealed())) {
                    model[i][j].reveal();

                    if (model[i][j].getNumAdjMines() == 0)
                        revealFieldRecursive(i, j);
                }
            }
        }
    }

    public Field getFieldContent(int x, int y) {
        return model[x][y];
    }

    public short checkResult() {
        short counter = 0;
        short numFlag = 0;
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                if ((model[i][j].isMine()) && (model[i][j].isRevealed())) {
                    showAllMines();
                    return -1;
                } else if (model[i][j].isFlagged()) {
                    if (!model[i][j].isMine()) {
                        showAllMines();
                        return -1;
                    } else
                        numFlag++;
                } else if ((!model[i][j].isMine()) && (model[i][j].isRevealed()))
                    counter++;
            }
        }
        if ((counter == rowNum * colNum - bombNum) || (numFlag == bombNum))
            return 1;

        return 0;
    }

    private void showAllMines() {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                if (model[i][j].isMine())
                    model[i][j].reveal();
            }
        }
    }
}