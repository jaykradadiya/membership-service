package com.membership.program.exception;

public class MembershipException extends RuntimeException {

    public MembershipException(String message) {
        super(message);
    }

    public MembershipException(String message, Throwable cause) {
        super(message, cause);
    }

}
