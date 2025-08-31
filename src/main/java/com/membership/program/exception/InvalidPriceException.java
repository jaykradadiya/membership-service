package com.membership.program.exception;

import org.springframework.http.HttpStatus;

public class InvalidPriceException extends MembershipException {
    
    public InvalidPriceException(String message) {
        super(message);
    }
}
