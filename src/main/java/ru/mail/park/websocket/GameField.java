package ru.mail.park.websocket;

public class GameField {

    private int[][] array;

    private int[] player1Selection;
    private int[] player2Selection;

    public GameField() {
        this.generateRandomField();
    }

    private void generateRandomField(){
        this.array = new int[][]{
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 1, 1, 0, 1, 1},
                {1, 0, 0, 1, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 1, 1, 1, 0, 1, 1},
                {1, 0, 1, 0, 1, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
        };
    }

    public int[][] getIntArray() {
        return this.array;
    }

    private boolean endTurn(){
        return this.player1Selection != null && this.player2Selection != null;
    }

    public boolean setPlayer1Selection(int xMin, int yMin, int xMax, int yMax) {
        this.player1Selection = new int[]{xMin, yMin, xMax, yMax};
        return this.endTurn();
    }

    public boolean setPlayer2Selection(int xMin, int yMin, int xMax, int yMax) {
        this.player2Selection = new int[]{xMin, yMin, xMax, yMax};
        return this.endTurn();
    }

    public void nextIteration() {
        //todo: recalc matrix;
    }

    public void clearUserSelections() {
        this.player2Selection = null;
        this.player1Selection = null;
    }

    public int[] getPlayer1Selection(){
        return this.player1Selection;
    }

    public int[] getPlayer2Selection(){
        return this.player2Selection;
    }
}
