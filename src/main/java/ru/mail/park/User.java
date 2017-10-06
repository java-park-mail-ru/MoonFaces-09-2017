package ru.mail.park;

public class User {

    private String login;
    private String email;
    private String passwordHash;

    @SuppressWarnings("unused")
    public User() { }

    public User(String login, String email, String password) {
        this.login = login;
        this.email = email;
        if(!password.isEmpty())
            passwordHash = PasswordHandler.passwordEncoder().encode(password);
        else
            passwordHash = null;
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

}