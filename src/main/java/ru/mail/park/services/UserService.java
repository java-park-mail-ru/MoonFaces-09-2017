package ru.mail.park.services;

import org.jetbrains.annotations.NotNull;
import ru.mail.park.models.User;

import java.util.List;


public interface UserService {
    void addUser(@NotNull User user);

    User getUser(@NotNull String login);

    User getUser(@NotNull Integer id);

    void changeLogin(Integer id, String newLogin);

    void changePassword(Integer id, String newPassword);

    void changeEmail(Integer id, String email);

    void changeScore(Integer id, Integer newScore);

    void updateScores(Integer id, Integer scores);

    List<User> getTopPlayers(int limit, int offset);
}
