package com.pranjal.grpc.server.aop;

import com.pranjal.grpc.server.exception.AlreadyExistsException;
import com.pranjal.grpc.server.exception.EntityNotFoundException;
import io.grpc.Status;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@GrpcAdvice
public class GrpcErrorAdvice {

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public Status handleEntityNotFoundException(EntityNotFoundException e) {
        return Status.fromCode(Status.Code.NOT_FOUND)
                     .withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(AlreadyExistsException.class)
    public Status handleRuntimeException(AlreadyExistsException e) {
        return Status.fromCode(Status.Code.ALREADY_EXISTS)
                     .withDescription(e.getMessage());
    }

    @GrpcExceptionHandler(Exception.class)
    public Status handleRuntimeException(Exception e) {
        return Status.fromCode(Status.Code.INTERNAL)
                     .withDescription(e.getMessage());
    }
}
