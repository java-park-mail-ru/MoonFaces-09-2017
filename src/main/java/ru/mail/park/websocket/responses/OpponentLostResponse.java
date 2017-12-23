package ru.mail.park.websocket.responses;

public class OpponentLostResponse {
    private String type;

    public OpponentLostResponse(String type) {
        this.type = type;
    }

    @SuppressWarnings("unused")
    public String getType() {
        return type;
    }
}
