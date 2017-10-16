package ru.mail.park.services;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.models.User;
import ru.mail.park.exceptions.UserExceptions;

public interface InterfaceUserService {
    void addUser(@NotNull User user) throws UserExceptions.UserAlreadyExists;
    User getUser(@NotNull String login);
    void changePassword(String login, String newPassword);
    void changeEmail(String login, String email);
}
