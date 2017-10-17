package ru.mail.park.exceptions;

public class UserExceptions {
    public static final class UserAlreadyExists extends Exception {
        public UserAlreadyExists(Throwable cause) {
            super(cause);
        }
    }
}
