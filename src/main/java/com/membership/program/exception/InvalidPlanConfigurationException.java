package com.membership.program.exception;

import org.springframework.http.HttpStatus;

public class InvalidPlanConfigurationException extends MembershipException {
    
    public InvalidPlanConfigurationException(String message) {
        super(message);
    }
}
