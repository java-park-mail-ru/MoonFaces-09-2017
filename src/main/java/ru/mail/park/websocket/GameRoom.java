package ru.mail.park.websocket;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.park.models.User;

import java.util.Objects;

public class GameRoom {

    private User player1;

    private User player2;

    private int player1Score;
    private int player2Score;

    private GameField gameField;

    public User getPlayer1() {
        return this.player1;
    }

    public User getPlayer2() {
        return this.player2;
    }

    public int getPlayer1Score() {
        return this.player1Score;
    }

    public int getPlayer2Score() {
        return this.player2Score;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public GameRoom(User owner) {
        this.player1 = owner;
    }

    public int playersCount() {
        int count = 1;
        if (player2 != null) {
            count++;
        }
        return count;
    }

    public void startGame() {
        this.gameField = new GameField();
    }

    public boolean isOpened() {
        return this.playersCount() < 2;
    }

    public int[][] getGameFieldForUser(User user) {
        if (Objects.equals(user.getLogin(), this.player1.getLogin())) {
            return this.gameField.getIntArray();
        }
        return this.gameField.getIntArrayInverted();
    }

    public boolean setUserSelection(User user, int cordXMin, int cordYMin, int cordXMax, int cordYMax) {
        boolean endTurn;
        if (Objects.equals(user.getLogin(), this.player1.getLogin())) {
            endTurn = this.gameField.setPlayer1Selection(cordXMin, cordYMin, cordXMax, cordYMax);
        } else {
            endTurn = this.gameField.setPlayer2Selection(cordXMin, cordYMin, cordXMax, cordYMax);
        }
        return endTurn;
    }

    public int[] invertSelection(int[] selection) {
        return new int[]{
                this.gameField.getFieldWidth() - selection[2] - 1,
                selection[1],
                this.gameField.getFieldWidth() - selection[0] - 1,
                selection[2 + 1],
        };
    }

    public void nextIteration() {
        this.gameField.nextIteration();
        this.player1Score = this.gameField.getPlayer1Scores();
        this.player2Score = this.gameField.getPlayer2Scores();
    }

    public User getOpponent(User user) {
        if (Objects.equals(user.getLogin(), this.player1.getLogin())) {
            return this.player2;
        }
        return this.player1;
    }

    public void clearUserSelections() {
        this.gameField.clearUserSelections();
    }

    public JSONObject getUserSelectionJsonString(User currentUser, User targetUser) throws JSONException {
        JSONObject data = new JSONObject();
        int[] selection;
        if (Objects.equals(targetUser.getLogin(), this.player1.getLogin())) {
            selection = this.gameField.getPlayer1Selection();
        } else {
            selection = this.gameField.getPlayer2Selection();
        }
        if (Objects.equals(currentUser.getLogin(), this.player2.getLogin())) {
            selection = this.invertSelection(selection);
        }
        data.put("xMin", selection[0]);
        data.put("yMin", selection[1]);
        data.put("xMax", selection[2]);
        data.put("yMax", selection[2 + 1]);
        return data;
    }

    public boolean gameOver() {
        if (this.player1Score / 2 > this.player2Score || this.player2Score / 2 > this.player1Score) {
            return true;
        }
        return false;
    }
}
