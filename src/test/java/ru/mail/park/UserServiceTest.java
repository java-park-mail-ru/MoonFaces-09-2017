package ru.mail.park;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.exceptions.UserExceptions;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTest extends Assert {

    @Autowired
    private UserService userService;

    @Test
    public void testUser() {
        final User user = new User("123", "123", "123", false);
        try {
            userService.addUser(user);
        } catch (UserExceptions.UserAlreadyExists userAlreadyExists) {
            assert false;
        }
        final User created = userService.getUser(user.getLogin());
        if(created != null) {
            assertEquals(user.getLogin(), created.getLogin());
            assertEquals(user.getPasswordHash(), created.getPasswordHash());
            assertEquals(user.getEmail(), created.getEmail());
        }
    }

    @Test
    public void testExistingUser() {
        final User user = new User("34", "13242323", "122353", false);
        try {
            userService.addUser(user);
        } catch (UserExceptions.UserAlreadyExists userAlreadyExists) {
            assert false;
        }
        try {
            userService.addUser(user);
        } catch (UserExceptions.UserAlreadyExists userAlreadyExists) {
            return;
        }
        assert false;
    }

    @Test
    public void testChangingUser() {
        final User user = new User("321", "312", "231", false);
        try {
            userService.addUser(user);
        } catch (UserExceptions.UserAlreadyExists userAlreadyExists) {
            assert false;
        }

        final String newPassword = "234";
        userService.changePassword(user.getLogin(), newPassword);

        final String newEmail = "234";
        userService.changeEmail(user.getLogin(), newEmail);

        final User created = userService.getUser(user.getLogin());
        if(created != null) {
            assertEquals(PasswordHandler.passwordEncoder().encode(newPassword), created.getPasswordHash());
            assertEquals(newEmail, created.getEmail());
        }
    }
}
