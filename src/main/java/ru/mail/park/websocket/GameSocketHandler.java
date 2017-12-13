package ru.mail.park.websocket;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;

import java.util.*;

public class GameSocketHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> SESSIONS = Collections.synchronizedSet(new HashSet<WebSocketSession>());
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);

    private final UserService userService;
    private SocketActionHandler socketActionHandler = null;

    public GameSocketHandler(UserService userService) {
        this.userService = userService;
        this.socketActionHandler = SocketActionHandler.getInstance(LOGGER, SESSIONS);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOGGER.warn("User Disconnected");
        SESSIONS.remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        final Integer id = (Integer) webSocketSession.getAttributes().get("id");
        if (id == null ||userService.getUser(id) == null) {
            LOGGER.error("Only authenticated users allowed to play a game");
            webSocketSession.close();
            return;
        }
        SESSIONS.add(webSocketSession);
        LOGGER.info(String.format("New user connection %s", userService.getUser(id).getLogin()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws Exception {
        User user = userService.getUser((Integer) webSocketSession.getAttributes().get("id"));
        LOGGER.info(String.format(
                "New message from @%s@  %s",
                user.getLogin(),
                message.getPayload()));
        socketActionHandler.handleActionJson(user, message.getPayload(), webSocketSession);
    }
}