package com.kramp.aggregator.web;

import com.kramp.aggregator.exception.CatalogServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CatalogServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleCatalogError(CatalogServiceException ex) {

        return new ApiError(
                Instant.now(),
                503,
                "Catalog service unavailable",
                ex.getMessage()
        );
    }

    @ExceptionHandler(InterruptedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInterrupted(InterruptedException ex) {
        return new ApiError(
                Instant.now(),
                503,
                "Service unavailable.",
                ex.getMessage()
        );
    }

    @ExceptionHandler(TimeoutException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleTimeout(TimeoutException ex) {
        return new ApiError(
                Instant.now(),
                503,
                "Service timeout.",
                ex.getMessage()
        );
    }
}
