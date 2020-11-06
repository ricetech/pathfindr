package com.gng2101groupb32.pathfindr.exceptions;

/**
 * Exception thrown when an attempt to perform a Firebase operation is made while
 * the user is not logged in.
 */
public class UserNotLoggedInException extends IllegalStateException {
    public UserNotLoggedInException(String message) {
        super(message);
    }
}
