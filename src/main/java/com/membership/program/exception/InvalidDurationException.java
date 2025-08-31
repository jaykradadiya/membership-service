package com.membership.program.exception;

import org.springframework.http.HttpStatus;

public class InvalidDurationException extends MembershipException {
    
    public InvalidDurationException(String message) {
        super(message);
    }
}
