package ru.mail.park.websocket;

import ru.mail.park.models.User;

public class GameRoom {

    public User player1;

    public User player2;

    public GameRoom(User owner) {
        this.player1 = owner;
    }

    public int playersCount() {
        int count = 0;
        if(player1 != null) count++;
        if(player2 != null) count++;
        return count;
    }

    public int[][] getGameField() {
        return new int[][]{
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
                {1, 0, 0, 0, 0, 0, 1, 1},
        };
    }

    public boolean isOpened() {
        return this.playersCount() < 2;
    }
}
