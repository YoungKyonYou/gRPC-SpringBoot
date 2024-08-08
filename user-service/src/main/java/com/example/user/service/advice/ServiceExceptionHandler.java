package com.example.user.service.advice;

import com.example.user.entity.exception.InsufficientBalanceException;
import com.example.user.entity.exception.InsufficientSharesException;
import com.example.user.entity.exception.UnknownTickerException;
import com.example.user.entity.exception.UnknownUserException;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

//GlobalExceptionHandelr 처럼 작동한다
@GrpcAdvice
public class ServiceExceptionHandler {
    @GrpcExceptionHandler(UnknownTickerException.class)
    public Status handleInvalidArgument(UnknownTickerException e){
        return Status.INVALID_ARGUMENT.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(UnknownUserException.class)
    public Status handleUnknownENtities(UnknownUserException e){
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

    @GrpcExceptionHandler({InsufficientBalanceException.class, InsufficientSharesException.class})
    public Status handlePreconditionFailures(Exception e){
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }
}
