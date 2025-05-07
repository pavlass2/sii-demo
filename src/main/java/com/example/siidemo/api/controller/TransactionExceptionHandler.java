package com.example.siidemo.api.controller;

import com.example.siidemo.api.dto.ClientErrorResponse;
import com.example.siidemo.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class TransactionExceptionHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ClientErrorResponse> handleNotFoundException(final NotFoundException ex)
    {
        LOGGER.debug("Requested resource was not found with message {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ClientErrorResponse(HttpStatus.NOT_FOUND.toString(), "Requested transaction was not found."));
    }
}
