package ru.mail.park.websocket;

import java.util.Arrays;
import java.util.Random;

public class GameField {

    private static final int FIELD_WIDTH = 8;

    private int[][] array;

    private int[] player1Selection;
    private int[] player2Selection;

    public GameField() {
        this.generateRandomField();
    }

    private void generateRandomField() {
        this.array = new int[FIELD_WIDTH][FIELD_WIDTH];
        for (int i = 0; i < FIELD_WIDTH; i++) {
            for (int j = 0; j < FIELD_WIDTH / 2; j++) {
                Random rand = new Random();
                int randomState = rand.nextInt(2);
                this.array[i][j] = randomState;
                this.array[i][FIELD_WIDTH - 1 - j] = randomState;
            }
        }
    }

    public int[][] getIntArray() {
        return this.array;
    }


    public int[][] getIntArrayInverted() {
        int[][] inverted = new int[FIELD_WIDTH][FIELD_WIDTH];
        for (int x = 0; x < this.array.length; x++) {
            for (int y = 0; y < this.array.length; y++) {
                inverted[y][x] = this.array[y][FIELD_WIDTH - x - 1];
            }
        }
        return inverted;
    }

    private boolean endTurn() {
        return this.player1Selection != null && this.player2Selection != null;
    }

    public boolean setPlayer1Selection(int cordXMin, int cordYMin, int cordXMax, int cordYMax) {
        this.player1Selection = new int[]{cordXMin, cordYMin, cordXMax, cordYMax};
        return this.endTurn();
    }

    public int[] getPlayer1Selection() {
        return this.player1Selection;
    }

    public boolean setPlayer2Selection(int cordXMin, int cordYMin, int cordXMax, int cordYMax) {
        this.player2Selection = new int[]{
                FIELD_WIDTH - cordXMax - 1,
                cordYMin,
                FIELD_WIDTH - cordXMin - 1,
                cordYMax
        };
        return this.endTurn();
    }

    public int[] getPlayer2Selection() {
        return this.player2Selection;
    }

    public int getFieldWidth() {
        return FIELD_WIDTH;
    }

    public void nextIteration() {
        int[][] updateMatrix = new int[this.getFieldWidth()][this.getFieldWidth()];
        for (int x = this.player1Selection[0]; x <= this.player1Selection[2]; x++) {
            for (int y = this.player1Selection[1]; y <= this.player1Selection[2 + 1]; y++) {
                updateMatrix[y][x] = 1;
            }
        }
        for (int x = this.player2Selection[0]; x <= this.player2Selection[2]; x++) {
            for (int y = this.player2Selection[1]; y <= this.player2Selection[2 + 1]; y++) {
                if (updateMatrix[y][x] == 1) {
                    updateMatrix[y][x] = 0;
                } else {
                    updateMatrix[y][x] = 1;
                }
            }
        }
        this.updateField(updateMatrix);
    }

    private void updateField(int[][] updateMatrix) {
        int[][] nextIteration = Arrays.copyOf(this.array, this.array.length);
        for (int i = 0; i < this.array.length; i++) {
            nextIteration[i] = Arrays.copyOf(this.array[i], this.array[i].length);
        }
        for (int x = 0; x < updateMatrix.length; x++) {
            for (int y = 0; y < updateMatrix[x].length; y++) {
                if (updateMatrix[x][y] == 1) {
                    int neighbours = this.countNeighbours(x, y);
                    if (neighbours < 2 || neighbours > 1 + 2) {
                        nextIteration[x][y] = 0;
                    } else if (this.array[x][y] == 0 && neighbours == 1 + 2) {
                        nextIteration[x][y] = 1;
                    }
                }
            }
        }
        this.array = nextIteration;
    }

    private int countNeighbours(int cordX, int cordY) {
        int count = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {

                int neighboorRow = trimCell(cordX + i);
                int neighboorCol = trimCell(cordY + j);

                if (!(i == 0 && j == 0) && this.array[neighboorRow][neighboorCol] == 1) {
                    count++;
                }
            }
        }

        return count;
    }

    private int trimCell(int cell) {
        if (cell == -1) {
            cell = FIELD_WIDTH - 1;
        }
        if (cell == FIELD_WIDTH) {
            cell = 0;
        }
        return cell;
    }

    public void clearUserSelections() {
        this.player2Selection = null;
        this.player1Selection = null;
    }
}
