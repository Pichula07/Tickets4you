package br.com.joaomurari.event4you.exception.handler;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import br.com.joaomurari.event4you.exception.ConflictException;
import br.com.joaomurari.event4you.exception.ExceptionResponse;
import br.com.joaomurari.event4you.exception.FeignExceptionNotFound;
import br.com.joaomurari.event4you.exception.ResourceNotFound;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponse> conflictFound(ConflictException ex) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), "Conflict!");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourceNotFound ex) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(), "Resource not found!");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignExceptionNotFound.class)
    public ResponseEntity<ExceptionResponse> handleFeignException(FeignExceptionNotFound ex) {
        ExceptionResponse response = new ExceptionResponse(new Date(), ex.getMessage(),
                "Error to find resource with Feign client.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
