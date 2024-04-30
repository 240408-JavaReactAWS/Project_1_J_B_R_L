package com.revature.StreamFlixBackend.exceptions;

public class InvalidMovieException extends RuntimeException{

        public InvalidMovieException(String e) {
            super(e);
        }
}
