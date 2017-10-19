package ru.mail.park.models;

import ru.mail.park.PasswordHandler;

public class User {

    private int id;
    private String login;
    private String email;
    private String password;
    private int score;

    @SuppressWarnings("unused")
    public User() {
    }

    public User(int id, String login, String email, String password, int score) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.password = password;
        this.score = score;
    }

    public User(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.score = 0;
    }

    public void evaluateHash() {
        this.password = PasswordHandler.passwordEncoder().encode(this.password);
    }

    public int getId() {
        return id;
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
        return password != null;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String passwordHash) {
        this.password = passwordHash;
    }

    public Integer getScore() {
        return score;
    }

    @SuppressWarnings("unused")
    public void setScore(Integer score) {
        this.score = score;
    }
}