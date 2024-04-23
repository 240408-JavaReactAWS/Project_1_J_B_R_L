package com.revature.StreamFlixBackend.exceptions;

public class UnauthorizedException  extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
