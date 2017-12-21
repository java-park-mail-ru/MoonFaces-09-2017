package ru.mail.park.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;
import ru.mail.park.websocket.responses.GameOverResponse;
import ru.mail.park.websocket.responses.JoinGameResponse;
import ru.mail.park.websocket.responses.UserSelectionResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class SocketActionHandler {

    private Logger logger = LoggerFactory.getLogger(SocketActionHandler.class);

    private static final String ACTION_CREATE_GAME = "CREATE_GAME";
    private static final String ACTION_JOIN_GAME = "JOIN_GAME";
    private static final String ACTION_SELECT_FIELD = "SELECT_FIELD";

    private HashMap<String, GameRoom> games = new HashMap<>();

    private HashMap<String, WebSocketSession> userSession = new HashMap<>();

    private HashMap<String, GameRoom> userGameRelation = new HashMap<>();

    private UserService userService;

    private static final int WINNER_POINTS = 5;

    private static final int LOOSER_POINTS = 1;

    public SocketActionHandler(UserService userService) {
        this.userService = userService;
    }

    public void registerUser(User user, WebSocketSession session) {
        this.userSession.put(user.getLogin(), session);
        this.sendMessage(session, this.getAllGamesAjaxString());
    }

    public void removeUser(User user) {
        if (userGameRelation.get(user.getLogin()) != null) {
            GameRoom room = userGameRelation.get(user.getLogin());
            if (room.playersCount() == 1) {
                userGameRelation.remove(user.getLogin());
                games.remove(room.getPlayer1().getLogin());
                this.notifyGameList();
            } else {
                JSONObject errorMessage = new JSONObject();
                try {
                    errorMessage.put("type", "OPPONENT_LOST");
                    User opponent = room.getPlayer1();
                    if (Objects.equals(room.getPlayer1().getLogin(), user.getLogin())) {
                        opponent = room.getPlayer2();
                    }
                    userGameRelation.remove(user.getLogin());
                    userGameRelation.remove(opponent.getLogin());
                    games.remove(room.getPlayer1().getLogin());
                    this.sendMessage(userSession.get(opponent.getLogin()), errorMessage.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
            } else if (Objects.equals(actionType, ACTION_SELECT_FIELD)) {
                this.selectField(user, webSocket, data);
            }
        } catch (JSONException e) {
            logger.error(String.format("Failed to parse json in %s", this.getClass().getSimpleName()));
        }
    }

    private void selectField(User user, WebSocketSession webSocket, JSONObject data) {
        GameRoom room = userGameRelation.get(user.getLogin());
        try {
            ObjectMapper mapper = new ObjectMapper();
            boolean endTurn = room.setUserSelection(
                    user,
                    data.getJSONObject("selection").getInt("xMin"),
                    data.getJSONObject("selection").getInt("yMin"),
                    data.getJSONObject("selection").getInt("xMax"),
                    data.getJSONObject("selection").getInt("yMax")
            );
            if (endTurn) {
                room.nextIteration();
                if (room.gameOver()) {
                    int player1Scores = room.getPlayer1Score();
                    int player2Scores = room.getPlayer2Score();
                    boolean player1Winner = false;
                    boolean player2Winner = false;
                    User winner;
                    User looser;
                    if (player1Scores > player2Scores) {
                        winner = room.getPlayer1();
                        looser = room.getPlayer2();
                        player1Winner = true;
                    } else {
                        winner = room.getPlayer2();
                        looser = room.getPlayer1();
                        player2Winner = true;
                    }
                    winner.setScore(winner.getScore() + WINNER_POINTS);
                    this.userService.updateScores(winner.getId(), winner.getScore());
                    looser.setScore(looser.getScore() + LOOSER_POINTS);
                    this.userService.updateScores(looser.getId(), looser.getScore());
                    GameOverResponse player1Response = new GameOverResponse(
                            "GAME_OVER",
                            player1Winner,
                            room.getGameFieldForUser(room.getPlayer1()),
                            room.getPlayer1().getScore()
                    );

                    GameOverResponse player2Response = new GameOverResponse(
                            "GAME_OVER",
                            player2Winner,
                            room.getGameFieldForUser(room.getPlayer2()),
                            room.getPlayer2().getScore()
                    );

                    this.sendMessage(userSession.get(room.getPlayer1().getLogin()), mapper.writeValueAsString(player1Response));
                    this.sendMessage(userSession.get(room.getPlayer2().getLogin()), mapper.writeValueAsString(player2Response));

                    this.games.remove(room.getPlayer1().toString());
                    this.userGameRelation.remove(room.getPlayer1().getLogin());
                    this.userGameRelation.remove(room.getPlayer2().getLogin());
                } else {
                    UserSelectionResponse player1Response = new UserSelectionResponse(
                            "FIELD_UPDATE",
                            room.getGameFieldForUser(user),
                            room.getUserSelectionJsonString(user, room.getOpponent(user)).toString()
                    );

                    UserSelectionResponse player2Response = new UserSelectionResponse(
                            "FIELD_UPDATE",
                            room.getGameFieldForUser(room.getOpponent(user)),
                            room.getUserSelectionJsonString(room.getOpponent(user), user).toString()
                    );


                    this.sendMessage(userSession.get(user.getLogin()), mapper.writeValueAsString(player1Response));
                    this.sendMessage(userSession.get(room.getOpponent(user).getLogin()), mapper.writeValueAsString(player2Response));
                    room.clearUserSelections();
                }
            }
        } catch (JSONException | JsonProcessingException e) {
            logger.error("Failed in field selection");
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
                room.setPlayer2(user);
                this.userGameRelation.put(user.getLogin(), room);

                room.startGame();

                ObjectMapper mapper = new ObjectMapper();

                JoinGameResponse ownerJoinResponse = new JoinGameResponse(
                        "OPPONENT_FOUND",
                        room.getGameFieldForUser(room.getPlayer1()),
                        user.getLogin()
                );

                JoinGameResponse opponentJoinResponse = new JoinGameResponse(
                        "CONNECTED",
                        room.getGameFieldForUser(room.getPlayer2()),
                        room.getPlayer1().getLogin()
                );

                this.sendMessage(webSocket, mapper.writeValueAsString(opponentJoinResponse));
                this.sendMessageToUser(room.getPlayer1(), mapper.writeValueAsString(ownerJoinResponse));
                this.notifyGameList();
            } else {
                this.sendMessage(webSocket, "Game is not empty");
            }
        } catch (JsonProcessingException e) {
            logger.error("Failed to join game");
        } catch (JSONException e) {
            logger.error("Failed to parse request");
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

    private void notifyGameList() {
        this.notifyAll(this.getAllGamesAjaxString());
    }

    private String getAllGamesAjaxString() {
        try {
            JSONObject gamesList = new JSONObject();
            for (Map.Entry<String, GameRoom> game : games.entrySet()) {
                if (games.get(game.getKey()).isOpened()) {
                    gamesList.put(game.getKey(), games.get(game.getKey()).getPlayer1().getScore());
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
        } catch (Exception e) {
            logger.error("Failed to send message");
        }
    }

    private void notifyAll(String message) {
        for (Map.Entry<String, WebSocketSession> session: this.userSession.entrySet()) {
            this.sendMessage(session.getValue(), message);
        }
    }
}
