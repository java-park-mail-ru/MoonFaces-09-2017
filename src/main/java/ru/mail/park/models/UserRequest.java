package ru.mail.park.models;

public class UserRequest {

    private String login;
    private String password;

    @SuppressWarnings("unused")
    public UserRequest() {
    }

    @SuppressWarnings("unused")
    public UserRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }


    public String getLogin() {
        return login;
    }

    @SuppressWarnings("unused")
    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    @SuppressWarnings("unused")
    public void setPassword(String password) {
        this.password = password;
    }
}