package ru.mail.park.services;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mail.park.PasswordHandler;
import ru.mail.park.models.User;
import ru.mail.park.exceptions.UserExceptions;
import org.jetbrains.annotations.NotNull;

@Service
public class UserService implements InterfaceUserService {
    private final JdbcTemplate template;

    @Autowired
    public UserService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void addUser(@NotNull User user) throws UserExceptions.UserAlreadyExists {
        try {
            template.update("INSERT INTO users(login, email, password) VALUES(?, ?, ?)", user.getLogin(), user.getEmail(), user.getPasswordHash());
        } catch (DuplicateKeyException e) {
            throw new UserExceptions.UserAlreadyExists(e);
        }
    }

    @Nullable
    @Override
    public User getUser(@NotNull String login) {
        try {
            return template.queryForObject("SELECT login, email, password FROM users WHERE login=?", USER_MAPPER, login);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void changePassword(String login, String newPassword) {
        final String newPasswordHash = PasswordHandler.passwordEncoder().encode(newPassword);
        template.update("UPDATE users SET password=? WHERE login=?", newPasswordHash, login);
    }

    @Override
    public void changeEmail(String login, String email) {
        template.update("UPDATE users SET email=? WHERE login=?", email, login);
    }

    private static final RowMapper<User> USER_MAPPER =
            (res, rowNum) -> new User(res.getString(1), res.getString(2), res.getString(3), true);
}

