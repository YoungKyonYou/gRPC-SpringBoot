package com.example.user.entity.exception;

public class UnknownTickerException extends RuntimeException{
    private static final String MESSAGE = "Ticker is not found";

    public UnknownTickerException() {
        super(MESSAGE);
    }
}
