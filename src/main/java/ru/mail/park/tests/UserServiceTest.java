package ru.mail.park.tests;

import org.junit.Assert;
import org.junit.Test;
import ru.mail.park.User;
import ru.mail.park.UserService;


public class UserServiceTest extends Assert {
    private UserService userService = new UserService();

    @Test
    public void testAddUserNull() {
        Assert.assertEquals(false, userService.addUser(null));
    }

    @Test
    public void testAddUserWithEmptyFields() {
        Assert.assertEquals(false, userService
                .addUser(new User("", "", "")));
    }

    @Test
    public void testAddCorrectUser() {
        User newUser = new User("test", "test@test.ru", "test");

        Assert.assertEquals(true, userService.addUser(newUser));
        Assert.assertEquals(newUser, userService.getUser("test"));
    }

    @Test
    public void testAddExistingUser() {
        testAddCorrectUser();
        User newUser = new User("test", "test@test.ru", "test");
        Assert.assertEquals(false, userService.addUser(newUser));
    }

    @Test
    public void testRemoveExistingUser() {
        testAddCorrectUser();
        Assert.assertEquals(true, userService.removeUser("test"));
        Assert.assertEquals(null, userService.getUser("test"));
    }

    @Test
    public void testRemoveNonExistingUser() {
        Assert.assertEquals(false, userService.removeUser(""));
    }

    @Test
    public void testGetExistingUser() {
        User newUser = new User("test", "test@test.ru", "test");
        Assert.assertEquals(true, userService.addUser(newUser));
        Assert.assertEquals(newUser, userService.getUser("test"));
    }

    @Test
    public void testGetNonExistingUser() {
        Assert.assertEquals(null, userService.getUser(""));
    }
}
