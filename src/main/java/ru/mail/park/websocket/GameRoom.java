package ru.mail.park.websocket;

import ru.mail.park.models.User;

public class GameRoom {

    public User player1;

    public User player2;

    public GameRoom(User owner){
        this.player1 = owner;
    }

    public int playersCount(){
        return 1;
    }
}
