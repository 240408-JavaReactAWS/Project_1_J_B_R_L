package com.revature.StreamFlixBackend.exceptions;

public class InvalidOTPException extends RuntimeException{

        public InvalidOTPException(String message) {
            super(message);
        }
}
