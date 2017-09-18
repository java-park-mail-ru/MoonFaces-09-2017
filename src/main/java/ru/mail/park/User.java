package ru.mail.park;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.atomic.AtomicLong;

@SuppressWarnings("unused")
public class User {
    private static final AtomicLong COUNTER = new AtomicLong(0);
    private final long id;

    @JsonProperty("login")
    private String login;

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    public User() {
        this.id = COUNTER.getAndIncrement();
    }

    public User(String login, String email, String password) {
        this.id = COUNTER.getAndIncrement();
        this.login = login;
        this.email = email;
        this.password = password;
    }

    @SuppressWarnings("unused")
    public long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    public void setLogin(String login) {
        this.login = login;
    }

    @SuppressWarnings("unused")
    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    @SuppressWarnings("unused")
    public String getPassword() {
        return password;
    }

    @SuppressWarnings("unused")
    public void setPassword(String password) {
        this.password = password;
    }
}