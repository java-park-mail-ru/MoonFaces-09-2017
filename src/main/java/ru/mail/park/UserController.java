package ru.mail.park;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;


@RestController
@CrossOrigin(origins = {"http://bacterio.herokuapp.com", "http://localhost:3000", "http://127.0.0.1:3000"})
public class UserController {

    private static final FailOrSuccessResponse OK_RESPONSE = new FailOrSuccessResponse(false, null);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/restapi/signup")
    public ResponseEntity<FailOrSuccessResponse> signUp(@RequestBody User body, HttpSession httpSession) {
        final String login = body.getLogin();
        final String email = body.getEmail();
        final String password = body.getPassword();

        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(email)
                || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailOrSuccessResponse(true, "Empty fields!"));
        }

        final User checkIfExist = userService.getUser(login);

        if (checkIfExist != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailOrSuccessResponse(true, "User already signed up!"));

        }

        userService.addUser(login, email, password);
        httpSession.setAttribute("login", login);

        return ResponseEntity.ok(OK_RESPONSE);
    }

    @PostMapping(path = "/restapi/signin")
    public ResponseEntity<FailOrSuccessResponse> signIn(@RequestBody User body, HttpSession httpSession) {
        final String login = body.getLogin();
        final String password = body.getPassword();

        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailOrSuccessResponse(true, "Empty fields!"));
        }

        final String checkIfSignedIn = (String) httpSession.getAttribute("login");

        if (checkIfSignedIn != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailOrSuccessResponse(true, "User already signed in!"));
        }

        final User registeredUser = userService.getUser(login);

        if (registeredUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailOrSuccessResponse(true, "This user is not signed up!"));
        }

        if (!registeredUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FailOrSuccessResponse(true, "Wrong password!"));
        }

        httpSession.setAttribute("login", login);
        return ResponseEntity.ok(OK_RESPONSE);
    }

    @PostMapping(path = "/restapi/logout")
    public ResponseEntity<FailOrSuccessResponse> logOut(HttpSession httpSession) {
        final String checkIfSignedIn = (String) httpSession.getAttribute("login");

        if (checkIfSignedIn == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailOrSuccessResponse(true, "You're not sighed in!"));
        }

        httpSession.invalidate();
        return ResponseEntity.ok(OK_RESPONSE);
    }

    @GetMapping(path = "/restapi/current")
    public ResponseEntity<?> currentUser(HttpSession httpSession) {
        final String currentUserLogin = (String) httpSession.getAttribute("login");

        if (currentUserLogin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailOrSuccessResponse(true, "Nobody is signed in!"));
        }

        return ResponseEntity.ok(new UserResponse(userService.getUser(currentUserLogin)));
    }

    @PostMapping(path = "/restapi/settings")
    public ResponseEntity<?> changeUser(@RequestBody User body, HttpSession httpSession) {
        final String email = body.getEmail();
        final String password = body.getPassword();

        final String currentUserLogin = (String) httpSession.getAttribute("login");

        if (currentUserLogin == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailOrSuccessResponse(true, "You're not signed in!"));
        }

        if (StringUtils.isEmpty(email)
                && StringUtils.isEmpty(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailOrSuccessResponse(true, "All fields are empty!"));
        }

        final User currentUser = userService.getUser(currentUserLogin);


        if (currentUser.getEmail().equals(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailOrSuccessResponse(true, "This is your current email!"));
        }

        if (currentUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailOrSuccessResponse(true, "This is your current password!"));
        }

        if (!StringUtils.isEmpty(email)) {
            currentUser.setEmail(email);
        }

        if (!StringUtils.isEmpty(password)) {
            currentUser.setPassword(password);
        }

        userService.addUser(currentUserLogin, currentUser);

        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    private static final class FailOrSuccessResponse {
        private final Boolean error;
        private final String description;

        private FailOrSuccessResponse(Boolean error, String description) {
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
        private final String login;
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
