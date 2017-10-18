package ru.mail.park.models;

import ru.mail.park.PasswordHandler;

public class User {

    private String login;
    private String email;
    private String passwordHash;
    private int score;

    @SuppressWarnings("unused")
    public User() {
    }

    public User(String login, String email, String passwordHash) {
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
        this.score = 0;
    }

    public void evaluateHash() {
        this.passwordHash = PasswordHandler.passwordEncoder().encode(this.passwordHash);
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

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Integer getScore() {
        return score;
    }

    @SuppressWarnings("unused")
    public void setScore(Integer score) {
        this.score = score;
    }
}