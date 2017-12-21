package ru.mail.park.websocket.responses;

public class JoinGameResponse {
    private String type;
    private int[][] gameField;
    private String opponentName;

    public JoinGameResponse(String type, int[][] gameField, String opponentName) {
        this.type = type;
        this.gameField = gameField;
        this.opponentName = opponentName;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return this.type;
    }

    @SuppressWarnings("unused")
    public int[][] getGameField() {
        return gameField;
    }

    @SuppressWarnings("unused")
    public String getOpponentName() {
        return opponentName;
    }

}
