package ru.mail.park.websocket.responses;

public class GameOverResponse {
    private String type;
    private boolean win;
    private int[][] gameField;
    private int score;

    public GameOverResponse(String type, boolean win, int[][] gameField, int score) {
        this.type = type;
        this.win = win;
        this.gameField = gameField;
        this.score = score;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public boolean isWin() {
        return win;
    }

    @SuppressWarnings("unused")
    public int[][] getGameField() {
        return gameField;
    }

    @SuppressWarnings("unused")
    public int getScore() {
        return score;
    }
}
