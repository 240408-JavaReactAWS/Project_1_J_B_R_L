package com.revature.StreamFlixBackend.exceptions;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String e) {
        super(e);
    }
}
