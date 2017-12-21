package ru.mail.park.websocket.responses;

import ru.mail.park.websocket.GameRoom;

import java.util.HashMap;
import java.util.Map;

public class GamesListResponse {

    private HashMap<String, Integer> gamesList = new HashMap<>();
    private String type;

    public GamesListResponse(String type, HashMap<String, GameRoom> games) {
        this.type = type;
        for (Map.Entry<String, GameRoom> game : games.entrySet()) {
            if (games.get(game.getKey()).isOpened()) {
                this.gamesList.put(game.getKey(), games.get(game.getKey()).getPlayer1().getScore());
            }
        }
    }

    @SuppressWarnings("unused")
    public HashMap<String, Integer> getGamesList() {
        return this.gamesList;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }
}
