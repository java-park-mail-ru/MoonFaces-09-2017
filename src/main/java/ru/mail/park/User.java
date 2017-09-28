package ru.mail.park;


public class User {

    private String login;
    private String email;
    private String password;
    private Integer score;

    @SuppressWarnings("unused")
    public User() { }

    public User(String login, String email, String password) {
        this.login = login;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getScore() {
        return score;
    }

    @SuppressWarnings("unused")
    public void setScore(Integer score) {
        this.score = score;
    }
}