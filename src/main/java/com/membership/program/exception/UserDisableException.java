package com.membership.program.exception;

public class UserDisableException extends RuntimeException {

    public UserDisableException(final Throwable cause){
        super("Exception User is disable",cause);
    }
}