package ru.mail.park.services;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.models.User;


public interface InterfaceUserService {
    void addUser(@NotNull User user);

    User getUser(@NotNull String login);

    void changePassword(Integer id, String newPassword);

    void changeEmail(Integer id, String email);
}
