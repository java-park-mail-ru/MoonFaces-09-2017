package ru.mail.park;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;

@Service
public class UserService {
    private final HashMap<String, User> registeredUser = new HashMap<String, User>();

    public boolean addUser(User user) {
        return user != null && !StringUtils.isEmpty(user.getLogin()) && 
                registeredUser.put(user.getLogin(), user) == null;
    }

    public User getUser(String login) {
        return registeredUser.get(login);
    }

    @SuppressWarnings("unused")
    public boolean removeUser(String login) {
        return registeredUser.remove(login) != null;
    }
}
