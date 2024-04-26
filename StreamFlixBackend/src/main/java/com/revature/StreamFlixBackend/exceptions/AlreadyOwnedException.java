package com.revature.StreamFlixBackend.exceptions;

public class AlreadyOwnedException extends RuntimeException{
    public AlreadyOwnedException(String e) {
        super(e);
    }
}
