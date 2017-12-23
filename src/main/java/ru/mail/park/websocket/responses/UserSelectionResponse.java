package ru.mail.park.websocket.responses;

public class UserSelectionResponse {
    private String type;
    private int[][] gameField;
    private String opponentSelection;

    public UserSelectionResponse(String type, int[][] gameField, String opponentSelection) {
        this.type = type;
        this.gameField = gameField;
        this.opponentSelection = opponentSelection;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public int[][] getGameField() {
        return gameField;
    }

    @SuppressWarnings("unused")
    public String getOpponentSelection() {
        return opponentSelection;
    }
}
