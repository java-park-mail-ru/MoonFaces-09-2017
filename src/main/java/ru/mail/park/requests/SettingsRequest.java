package ru.mail.park.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class SettingsRequest {
    private final String email;
    private final String password;

    @JsonCreator
    public SettingsRequest(@JsonProperty("email") String email,
                           @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public String getPassword() {
        return password;
    }
}
