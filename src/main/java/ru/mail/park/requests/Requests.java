package ru.mail.park.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Requests {

    public static String makeJson(Object requestObject) {
        String result;
        try {
            result = new ObjectMapper().writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            result = "";
        }
        return result;
    }

    public static final class SignupRequest {
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

    public static final class SigninRequest {
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

    public static final class SettingsRequest {
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
}