package ru.mail.park.models;

import ru.mail.park.PasswordHandler;

public class User {

    private String login;
    private String email;
    private String passwordHash;
    private Integer score;

    @SuppressWarnings("unused")
    public User() {
    }

    public User(String login, String email, String password, boolean encoded) {
        this.login = login;
        this.email = email;
        if (!password.isEmpty()) {
            if (!encoded) {
                passwordHash = PasswordHandler.passwordEncoder().encode(password);
            } else {
                passwordHash = password;
            }
        } else {
            passwordHash = null;
        }
        this.score = 0;
    }

    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean hasPassword() {
        return passwordHash != null;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = PasswordHandler.passwordEncoder().encode(password);
    }

    public Integer getScore() {
        return score;
    }

    @SuppressWarnings("unused")
    public void setScore(Integer score) {
        this.score = score;
    }
}