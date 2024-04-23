package com.revature.StreamFlixBackend.exceptions;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String e)
    {
        super(e);
    }
}
