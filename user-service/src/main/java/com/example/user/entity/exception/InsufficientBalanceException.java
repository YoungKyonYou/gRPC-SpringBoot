package com.example.user.entity.exception;

public class InsufficientBalanceException extends RuntimeException{
    private static final String MESSAGE = "User [id=%d] does not have enough fund to complete the trasaction";

    public InsufficientBalanceException(Integer userId) {
        super(MESSAGE.formatted(userId));
    }
}
