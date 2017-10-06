package ru.mail.park;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {
    private final HashMap<String, User> registeredUser = new HashMap<String, User>();

    public void addUser(User user) {
        registeredUser.put(user.getLogin(), user);
    }

    public void addUser(String login, User user) {
        registeredUser.put(login, user);
    }

    public User getUser(String login) {
        return registeredUser.get(login);
    }

    @SuppressWarnings("unused")
    public void removeUser(String login) {
        registeredUser.remove(login);
    }
}
