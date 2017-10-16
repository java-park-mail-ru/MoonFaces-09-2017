package ru.mail.park.models;

public class SettingsRequest {

    private String email;
    private String password;

    @SuppressWarnings("unused")
    public SettingsRequest() {
    }

    @SuppressWarnings("unused")
    public SettingsRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    @SuppressWarnings("unused")
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    @SuppressWarnings("unused")
    public void setPassword(String password) {
        this.password = password;
    }
}