package com.revature.StreamFlixBackend.exceptions;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(String e) {
        super(e);
    }
}
