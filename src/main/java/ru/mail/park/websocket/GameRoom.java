package ru.mail.park.websocket;

import org.json.JSONException;
import org.json.JSONObject;
import ru.mail.park.models.User;

import java.util.Objects;

public class GameRoom {

    public User player1;

    public User player2;

    private GameField gameField;

    public GameRoom(User owner) {
        this.player1 = owner;
    }

    public int playersCount() {
        int count = 0;
        if(player1 != null) count++;
        if(player2 != null) count++;
        return count;
    }

    public void startGame(){
        this.gameField = new GameField();
    }

    public int[][] getGameField() {
        return this.gameField.getIntArray();
    }

    public boolean isOpened() {
        return this.playersCount() < 2;
    }

    public int[][] getGameFieldForUser(User user) {
        if(Objects.equals(user.getLogin(), this.player1.getLogin())){
            return this.getGameField();
        }
        int tmp;
        int[][] array = this.getGameField();
        for(int i = 0; i< array.length; i++){
            for(int j = 0; j < array[i].length/2; j++){
                tmp = array[i][j];
                array[i][j] = array[i][array[i].length - j -1];
                array[i][array[i].length - j -1] = tmp;
            }
        }
        return array;
    }

    public boolean setUserSelection(User user, int xMin, int yMin, int xMax, int yMax) {
        boolean endTurn;
        if(Objects.equals(user.getLogin(), this.player1.getLogin())){
            endTurn = this.gameField.setPlayer1Selection(xMin, yMin, xMax, yMax);
        }else{
            endTurn = this.gameField.setPlayer2Selection(7 - xMin, yMin, 7 - xMax, yMax);
        }
        return endTurn;
    }

    public void nextIteration() {
        this.gameField.nextIteration();
    }

    public User getOpponent(User user) {
        if(Objects.equals(user.getLogin(), this.player1.getLogin())){
            return this.player2;
        }
        return this.player1;
    }

    public void clearUserSelections(){
        this.gameField.clearUserSelections();
    }

    public JSONObject getUserSelectionJsonString(User user) throws JSONException {
        JSONObject data = new JSONObject();
        if(Objects.equals(user.getLogin(), this.player1.getLogin())){
            int[] selection = this.gameField.getPlayer2Selection();
            data.put("xMin", selection[2]);// WHY CHANGE POSITIONS????
            data.put("yMin", selection[1]);
            data.put("xMax", selection[0]);
            data.put("yMax", selection[3]);
        }else {
            int[] selection = this.gameField.getPlayer1Selection();
            data.put("xMin", 7 - selection[2]);// WHY CHANGE POSITIONS????
            data.put("yMin", selection[1]);
            data.put("xMax", 7 - selection[0]);
            data.put("yMax", selection[3]);
        }
        return data;
    }
}
