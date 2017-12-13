package ru.mail.park.websocket;

import org.json.JSONArray;
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

    private HashMap<String, WebSocketSession> userSession = new HashMap<String, WebSocketSession>();

    private static SocketActionHandler instance = null;

    public SocketActionHandler(Logger logger, Set<WebSocketSession> sessions) {
        this.LOGGER = logger;
        this.SESSIONS = sessions;
    }


    public static SocketActionHandler getInstance(Logger logger, Set<WebSocketSession> sessions) {
        if (instance == null) {
            instance = new SocketActionHandler(logger, sessions);
        }
        return instance;
    }

    public void registerUser(User user, WebSocketSession session) {
        this.userSession.put(user.getLogin(), session);
    }

    public void removeUser(User user) {
        this.userSession.remove(user.getLogin());
    }

    public void handleActionJson(User user, String message, WebSocketSession webSocket) {
        try {
            JSONObject data = new JSONObject(message);
            String actionType = (String) data.get("type");
            if (Objects.equals(actionType, ACTION_CREATE_GAME)) {
                this.createGame(user, webSocket);
            } else if (Objects.equals(actionType, ACTION_JOIN_GAME)) {
                this.joinGame(user, webSocket, data);
            }
        } catch (JSONException e) {
            LOGGER.error(String.format("Failed to parse json in %s", this.getClass().getSimpleName()));
        }
    }

    private void joinGame(User user, WebSocketSession webSocket, JSONObject data) {
        try {
            GameRoom room = games.get(data.get("player"));
            if (room == null) {
                this.sendMessage(webSocket, "Game not found");
                return;
            }
            if (room.playersCount() < 2) {
                room.player2 = user;

                JSONObject response = new JSONObject();
                JSONObject ownerResponse = new JSONObject();
                JSONArray jsonGameField = new JSONArray(room.getGameField());

                response.put("status", "connected");
                response.put("game_field", jsonGameField);

                ownerResponse.put("status", "opponent_found");
                ownerResponse.put("game_field", jsonGameField);

                this.sendMessage(webSocket, response.toString());
                this.sendMessageToUser(room.player1, ownerResponse.toString());
                this.notifyGameList();
            } else {
                this.sendMessage(webSocket, "Game is not empty");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMessageToUser(User player1, String ownerResponse) {
        this.sendMessage(userSession.get(player1.getLogin()), ownerResponse);
    }

    private void createGame(User user, WebSocketSession webSocket) {
        games.put(user.getLogin(), new GameRoom(user));
        this.notifyGameList();
    }

    private void notifyGameList(){
        try {
            JSONObject gamesList = new JSONObject();
            for (Map.Entry<String, GameRoom> game : games.entrySet()) {
                if(games.get(game.getKey()).isOpened()) {
                    gamesList.put(game.getKey(), games.get(game.getKey()).playersCount());
                }
            }
            gamesList = new JSONObject().put("type", "UPDATE_GAMES_LIST").put("games", gamesList);

            this.notifyAll(gamesList.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            LOGGER.error("Failed to send message");
        }
    }

    private void notifyAll(String message) {
        for (WebSocketSession session : SESSIONS) {
            this.sendMessage(session, message);
        }
    }
}
