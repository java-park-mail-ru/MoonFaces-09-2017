package ru.mail.park.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SigninRequest {
    private final String login;
    private final String password;

    @JsonCreator
    public SigninRequest(@JsonProperty("login") String login,
                         @JsonProperty("password") String password) {
        this.login = login;
        this.password = password;
    }

    @SuppressWarnings("unused")
    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    public String getPassword() {
        return password;
    }
}
