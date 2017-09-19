package ru.mail.park;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@CrossOrigin(origins = {"http://bacterio.herokuapp.com", "http://localhost:3000", "http://127.0.0.1:3000"})
public class UserController {

    private static final FailResponse OK_RESPONSE = new FailResponse(false, null);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/restapi/signup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity signUp(@RequestBody User body) {
        final String login = body.getLogin();
        final String email = body.getEmail();
        final String password = body.getPassword();

        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(email)
                || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailResponse(true, "Empty fields!"));
        }

        final User checkIfExist = userService.getUser(login);

        if (checkIfExist != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailResponse(true, "User already signed up!"));

        }

        userService.addUser(login, email, password);

        return ResponseEntity.ok(OK_RESPONSE);
    }

    @RequestMapping(path = "/restapi/signin", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity signIn(@RequestBody User body, HttpSession httpSession) {
        final String login = body.getLogin();
        final String password = body.getPassword();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailResponse(true, "Empty fields!"));
        }

        final String checkIfSignedIn = (String) httpSession.getAttribute("login");

        if (checkIfSignedIn != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailResponse(true, "User already signed in!"));
        }

        final User registeredUser = userService.getUser(login);

        if (registeredUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailResponse(true, "This user is not signed up!"));
        }

        if (!registeredUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FailResponse(true, "Wrong password!"));
        }

        httpSession.setAttribute("login", login);
        return ResponseEntity.ok(OK_RESPONSE);
    }

    @RequestMapping(path = "/restapi/logout", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity logOut(HttpSession httpSession) {
        final String checkIfSignedIn = (String) httpSession.getAttribute("login");

        if (checkIfSignedIn == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailResponse(true, "You're not sighed in!"));
        }

        httpSession.removeAttribute("login");
        return ResponseEntity.ok(OK_RESPONSE);
    }

    @RequestMapping(path = "/restapi/current", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity currentUser(HttpSession httpSession) {
        final String currentUserLogin = (String) httpSession.getAttribute("login");

        if (currentUserLogin == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailResponse(true, "Nobody is signed in!"));
        }

        return ResponseEntity.ok(new UserResponse(userService.getUser(currentUserLogin)));
    }

    @RequestMapping(path = "/restapi/settings", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity changeUser(@RequestBody User body, HttpSession httpSession) {
        final String email = body.getEmail();
        final String password = body.getPassword();

        final String currentUserLogin = (String) httpSession.getAttribute("login");

        if (currentUserLogin == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailResponse(true, "You're not signed in!"));
        }

        if (StringUtils.isEmpty(email)
                && StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailResponse(true, "All fields are empty!"));
        }

        final User currentUser = userService.getUser(currentUserLogin);


        if (currentUser.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailResponse(true, "This is your current email!"));
        }

        if (currentUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailResponse(true, "This is your current password!"));
        }

        if (!StringUtils.isEmpty(email)) {
            currentUser.setEmail(email);
        }

        if (!StringUtils.isEmpty(password)) {
            currentUser.setPassword(password);
        }

        userService.removeUser(currentUserLogin);
        userService.addUser(currentUserLogin, currentUser);

        return ResponseEntity.ok(new UserResponse(userService.getUser(currentUserLogin)));
    }

    private static final class FailResponse {
        @JsonProperty("error")
        private final Boolean error;

        @JsonProperty("description")
        private final String description;

        private FailResponse(Boolean error, String description) {
            this.error = error;
            this.description = description;
        }

        @SuppressWarnings("unused")
        public Boolean getError() {
            return error;
        }

        @SuppressWarnings("unused")
        public String getDescription() {
            return description;
        }
    }

    private static final class UserResponse {
        @JsonProperty("login")
        private final String login;

        @JsonProperty("email")
        private final String email;

        private UserResponse(User user) {
            this.login = user.getLogin();
            this.email = user.getEmail();
        }

        @SuppressWarnings("unused")
        public String getLogin() {
            return login;
        }

        @SuppressWarnings("unused")
        public String getEmail() {
            return email;
        }
    }
}
