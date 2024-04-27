package com.revature.StreamFlixBackend.exceptions;

public class OTPExpirationException extends RuntimeException{
    public OTPExpirationException(String e) {
        super(e);
    }
}
