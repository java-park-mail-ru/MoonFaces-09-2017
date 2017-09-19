package ru.mail.park;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {
    private final HashMap<String, User> registeredUser = new HashMap<String, User>();

    @SuppressWarnings("unused")
    public void addUser(String login, String email, String password) {
        registeredUser.put(login, new User(login, email, password));
    }

    @SuppressWarnings("unused")
    public void addUser(String login, User user) {
        registeredUser.put(login, user);
    }

    @SuppressWarnings("unused")
    public User getUser(String login) {
        return registeredUser.get(login);
    }

    @SuppressWarnings("unused")
    public void removeUser(String login) {
        registeredUser.remove(login);
    }
}
