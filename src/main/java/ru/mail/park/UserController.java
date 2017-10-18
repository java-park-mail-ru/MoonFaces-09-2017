package ru.mail.park;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.exceptions.UserAlreadyExists;
import ru.mail.park.models.User;
import ru.mail.park.models.UserRequest;
import ru.mail.park.requests.SettingsRequest;
import ru.mail.park.services.UserService;

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
    public ResponseEntity<FailOrSuccessResponse> signUp(@RequestBody User user) {
        final String login = user.getLogin();
        final String email = user.getEmail();

        if (StringUtils.isEmpty(login)
                || StringUtils.isEmpty(email)
                || !user.hasPassword()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new FailOrSuccessResponse(true, "Empty fields!"));
        }
        user.evaluateHash();
        try {
            userService.addUser(user);
        } catch (UserAlreadyExists userAlreadyExists) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailOrSuccessResponse(true, "User already signed up!"));
        }
        return ResponseEntity.ok(OK_RESPONSE);
    }

    @PostMapping(path = "/restapi/signin")
    public ResponseEntity<?> signIn(@RequestBody UserRequest body, HttpSession httpSession) {

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

        if (!PasswordHandler.passwordEncoder().matches(password, registeredUser.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FailOrSuccessResponse(true, "Wrong password!"));
        }

        httpSession.setAttribute("login", login);
        return ResponseEntity.ok(new UserResponse(registeredUser));
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailOrSuccessResponse(true, "Nobody is signed in!"));
        }

        final User currentUser = userService.getUser(currentUserLogin);
        if (currentUser != null) {
            return ResponseEntity.ok(new UserResponse(currentUser));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailOrSuccessResponse(true, "User is not signed up! "));
        }
    }

    @PostMapping(path = "/restapi/settings")
    public ResponseEntity<?> changeUser(@RequestBody SettingsRequest body, HttpSession httpSession) {
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

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailOrSuccessResponse(true, "This user is not signed up!"));
        }

        if (!StringUtils.isEmpty(password)) {
            userService.changePassword(currentUserLogin, password);
            currentUser.setPasswordHash(password);
        }
        if (!StringUtils.isEmpty(password)) {
            userService.changeEmail(currentUserLogin, email);
            currentUser.setEmail(email);
        }


        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    private static final class FailOrSuccessResponse {
        private final Boolean error;
        private final String description;

        private FailOrSuccessResponse(@NotNull Boolean error, @Nullable String description) {
            this.error = error;
            this.description = description;
        }

        @SuppressWarnings("unused")
        public Boolean getError() {
            return error;
        }

        @SuppressWarnings("unused")
        @Nullable
        public String getDescription() {
            return description;
        }
    }

    private static final class UserResponse {
        private final String login;
        private final String email;
        private final Integer score;

        private UserResponse(@NotNull User user) {
            this.login = user.getLogin();
            this.email = user.getEmail();
            this.score = user.getScore();
        }

        @SuppressWarnings("unused")
        public String getLogin() {
            return login;
        }

        @SuppressWarnings("unused")
        public String getEmail() {
            return email;
        }

        @SuppressWarnings("unused")
        public Integer getScore() {
            return score;
        }
    }
}
