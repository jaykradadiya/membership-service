package com.membership.program.exception;

import org.springframework.http.HttpStatus;

public class SubscriptionException extends MembershipException {

    public SubscriptionException(String message){
        super(message);
    }
}
