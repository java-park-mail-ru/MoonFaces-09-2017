package ru.mail.park.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SignupRequest {
    private final String login;
    private final String password;
    private final String email;

    @JsonCreator
    public SignupRequest(@JsonProperty("login") String login,
                         @JsonProperty("password") String password,
                         @JsonProperty("email") String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    @SuppressWarnings("unused")
    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    public String getPassword() {
        return password;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }
}
