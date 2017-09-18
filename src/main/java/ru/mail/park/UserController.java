package ru.mail.park;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;


@RestController
public class UserController {

    private static final String OK_RESPONSE = "{\"error\": false}";

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
                    .body(new FailedResponse("Empty fields!"));
        }

        final User checkIfExist = userService.getUser(login);

        if (checkIfExist != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailedResponse("User already signed up!"));

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
                    .body(new FailedResponse("Empty fields!"));
        }

        final String checkIfSignedIn = (String) httpSession.getAttribute("login");

        if (checkIfSignedIn != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailedResponse("User already signed in!"));
        }

        final User registeredUser = userService.getUser(login);

        if (registeredUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new FailedResponse("This user is not signed up!"));
        }

        if (!registeredUser.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new FailedResponse("Wrong password!"));
        } else {
            httpSession.setAttribute("login", login);
        }

        return ResponseEntity.ok(OK_RESPONSE);
    }

    @RequestMapping(path = "/restapi/logout", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity logOut(HttpSession httpSession) {
        final String checkIfSignedIn = (String) httpSession.getAttribute("login");

        if (checkIfSignedIn == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new FailedResponse("You're not sighed in!"));
        }

        httpSession.removeAttribute("login");
        return ResponseEntity.ok(OK_RESPONSE);
    }

    private static final class FailedResponse {
        private final Boolean error;
        private final String description;

        private FailedResponse(String error) {
            this.error = true;
            this.description = error;
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
}