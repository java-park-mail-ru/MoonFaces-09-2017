package ru.mail.park.websocket;

import org.slf4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SocketActionHandler {

    private Logger LOGGER;


    private static final String
            ACTION_CREATE_GAME = "CREATE_GAME",
            ACTION_JOIN_GAME = "JOIN_GAME";

    private Map<String, Object> actions = new HashMap<String, Object>();

    private HashMap<String, GameRoom> games = new HashMap<String, GameRoom>();

    private Set<WebSocketSession> SESSIONS;

    private static SocketActionHandler instance = null;

    public SocketActionHandler(Logger logger, Set<WebSocketSession> sessions){
        this.LOGGER = logger;
        this.SESSIONS = sessions;
    }


    public static SocketActionHandler getInstance(Logger logger, Set<WebSocketSession> sessions){
        if(instance == null) {
            instance = new SocketActionHandler(logger, sessions);
        }
        return instance;
    }

    public void handleActionJson(User user, String message,WebSocketSession webSocket){
        try {
            JSONObject data = new JSONObject(message);
            String actionType = (String) data.get("type");
            if(Objects.equals(actionType, ACTION_CREATE_GAME)){
                this.createGame(user, webSocket);
            }else if(Objects.equals(actionType, ACTION_JOIN_GAME)){
                this.joinGame(user, webSocket, data);
            }
        } catch (JSONException e) {
            LOGGER.error(String.format("Failed to parse json in %s", this.getClass().getSimpleName()));
        }
    }

    private void joinGame(User user, WebSocketSession webSocket, JSONObject data) {

    }

    private void createGame(User user, WebSocketSession webSocket){


        games.put(user.getLogin(), new GameRoom(user));

        try {
            JSONObject gamesList = new JSONObject();
            for (Map.Entry<String, GameRoom> game : games.entrySet()) {
                gamesList.put(game.getKey(), games.get(game.getKey()).playersCount());
            }
            gamesList = new JSONObject().put("type", "UPDATE_GAMES_LIST").put("games", gamesList);

            this.notifyAll(gamesList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void notifyAll(String message){
        for(WebSocketSession session: SESSIONS){
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                LOGGER.error("Failed to send message");
            }
        }
    }
}
