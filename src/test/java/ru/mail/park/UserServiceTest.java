package ru.mail.park;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mail.park.exceptions.UserAlreadyExists;
import ru.mail.park.models.User;
import ru.mail.park.services.UserService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTest extends Assert {

    @Autowired
    private JdbcTemplate template;

    @Autowired
    private UserService userService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @After
    public void clearTestTable() {
        template.execute("TRUNCATE TABLE users");
    }

    @Test
    public void testUser() throws UserAlreadyExists {
        final User user = new User("123", "123", "123");
        userService.addUser(user);
        final User created = userService.getUser(user.getLogin());
        assertNotNull(created);
        assertEquals(user.getLogin(), created.getLogin());
        assertEquals(user.getPassword(), created.getPassword());
        assertEquals(user.getEmail(), created.getEmail());
    }

    @Test
    public void testExistingUser() throws UserAlreadyExists {
        final User user = new User("34", "13242323", "122353");
        userService.addUser(user);
        expectedException.expect(RuntimeException.class);
        userService.addUser(user);
        assert false;
    }

    @Test
    public void testChangingUser() throws UserAlreadyExists{
        final User user = new User("321", "312", "231");
        userService.addUser(user);

        final String newPassword = "234";
        userService.changePassword(user.getLogin(), newPassword);

        final String newEmail = "234";
        userService.changeEmail(user.getLogin(), newEmail);

        final User created = userService.getUser(user.getLogin());
        if(created != null) {
            assertTrue(PasswordHandler.passwordEncoder().matches(newPassword, created.getPassword()));
            assertEquals(newEmail, created.getEmail());
        }
    }
}
