package com.revature.StreamFlixBackend.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String e) {
        super(e);   
    }
}