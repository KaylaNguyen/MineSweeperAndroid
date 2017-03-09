package hu.ait.minesweeperandroid.model;

/**
 * Created by khue on 3/6/17.
 */

public class Field {
    private boolean isRevealed;
    private boolean isMine;
    private int numAdjMines;
    private boolean isFlagged;

    public Field() {
        this.isRevealed = false;
        this.numAdjMines = -1;
        this.isMine = false;
        this.isFlagged = false;
    }

    public boolean isRevealed() {
        return this.isRevealed;
    }

    public void reveal() {
        this.isRevealed = true;
    }

    public void setNumMines(int numMines) {
        this.numAdjMines = numMines;
    }

    public int getNumAdjMines() {
        return this.numAdjMines;
    }

    public boolean isMine() {
        return this.isMine;
    }

    public boolean isFlagged() {
        return this.isFlagged;
    }

    public void flag() {
        this.isFlagged = true;
    }

    public void placeMine() {
        this.isMine = true;
    }
}