package ru.mail.park.websocket;

import org.json.JSONArray;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
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
            ACTION_JOIN_GAME = "JOIN_GAME",
            ACTION_SELECT_FIELD = "SELECT_FIELD";

    private HashMap<String, GameRoom> games = new HashMap<>();

    private Set<WebSocketSession> SESSIONS;

    private HashMap<String, WebSocketSession> userSession = new HashMap<>();

    private HashMap<String, GameRoom> userGameRelation = new HashMap<>();

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
        this.sendMessage(session, this.getAllGamesAjaxString());
    }

    public void removeUser(User user) {
        if (userGameRelation.get(user.getLogin()) != null) {
            //todo: delete game + this user looses
        }
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
            }else if (Objects.equals(actionType, ACTION_SELECT_FIELD)) {
                this.selectField(user, webSocket, data);
            }
        } catch (JSONException e) {
            LOGGER.error(String.format("Failed to parse json in %s", this.getClass().getSimpleName()));
        }
    }

    private void selectField(User user, WebSocketSession webSocket, JSONObject data) {
        GameRoom room = userGameRelation.get(user.getLogin());
        try {
            boolean endTurn = room.setUserSelection(
                    user,
                    data.getJSONObject("selection").getInt("xMin"),
                    data.getJSONObject("selection").getInt("yMin"),
                    data.getJSONObject("selection").getInt("xMax"),
                    data.getJSONObject("selection").getInt("yMax")
            );
            if(endTurn){
                room.nextIteration();
                JSONObject player1Response = new JSONObject();
                JSONObject player2Response = new JSONObject();

                player1Response.put("game_field", new JSONArray(room.getGameFieldForUser(user)));
                player2Response.put("game_field", new JSONArray(room.getGameFieldForUser(room.getOpponent(user))));

                player1Response.put("opponent_selection", room.getUserSelectionJsonString(room.getOpponent(user)));
                player2Response.put("opponent_selection", room.getUserSelectionJsonString(user));

                player1Response.put("type", "FIELD_UPDATE");
                player2Response.put("type", "FIELD_UPDATE");

                this.sendMessage(userSession.get(user.getLogin()), player2Response.toString());
                this.sendMessage(userSession.get(room.getOpponent(user).getLogin()), player1Response.toString());
                room.clearUserSelections();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                this.userGameRelation.put(user.getLogin(), room);

                room.startGame();

                JSONObject response = new JSONObject();
                JSONObject ownerResponse = new JSONObject();
                JSONArray ownerJsonGameField = new JSONArray(room.getGameFieldForUser(room.player1));
                JSONArray opponentJsonGameField = new JSONArray(room.getGameFieldForUser(room.player2));

                response.put("type", "CONNECTED");
                response.put("game_field", ownerJsonGameField);
                response.put("opponent", room.player1.getLogin());

                ownerResponse.put("type", "OPPONENT_FOUND");
                ownerResponse.put("game_field", opponentJsonGameField);
                ownerResponse.put("opponent", user.getLogin());

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
        this.userGameRelation.put(user.getLogin(), games.get(user.getLogin()));
        this.notifyGameList();
    }

    private void notifyGameList(){
        this.notifyAll(this.getAllGamesAjaxString());
    }

    private String getAllGamesAjaxString(){
        try {
            JSONObject gamesList = new JSONObject();
            for (Map.Entry<String, GameRoom> game : games.entrySet()) {
                if(games.get(game.getKey()).isOpened()) {
                    gamesList.put(game.getKey(), games.get(game.getKey()).player1.getScore());
                }
            }
            return new JSONObject().put("type", "UPDATE_GAMES_LIST").put("games", gamesList).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
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
