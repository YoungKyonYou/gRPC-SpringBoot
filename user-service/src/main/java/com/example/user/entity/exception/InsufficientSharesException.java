package com.example.user.entity.exception;

public class InsufficientSharesException extends RuntimeException{
    private static final String MESSAGE = "User [id=%d] does not have enough shares to complete the trasaction";

    public InsufficientSharesException(Integer userId) {
        super(MESSAGE.formatted(userId));
    }
}
