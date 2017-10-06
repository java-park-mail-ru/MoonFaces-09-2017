package ru.mail.park;

import org.junit.Assert;
import org.junit.Test;


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
    public void testAddUserCorrectUser() {
        User newUser = new User("test", "test@test.ru", "test");

        Assert.assertEquals(false, userService.addUser(newUser));
        Assert.assertEquals(newUser, userService.getUser("test"));
    }

    @Test
    public void testAddExistingUser() {
        testAddUserCorrectUser();
        User newUser = new User("test", "test@test.ru", "test");
        Assert.assertEquals(true, userService.addUser(newUser));
    }

    @Test
    public void testRemoveExistingUser() {
        testAddUserCorrectUser();
        Assert.assertEquals(true, userService.removeUser("test"));
        Assert.assertEquals(null, userService.getUser("test"));
    }

    @Test
    public void testRemoveNotExistingUser() {
        Assert.assertEquals(false, userService.removeUser(""));
    }


}
