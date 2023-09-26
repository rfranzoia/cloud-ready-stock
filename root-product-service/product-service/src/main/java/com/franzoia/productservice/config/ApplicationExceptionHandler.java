package com.franzoia.productservice.config;

import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.ErrorResponse;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ServiceNotAvailableException.class, FeignException.class})
    public ResponseEntity<ErrorResponse> handleProductServiceNotAvailableException(ServiceNotAvailableException exception, WebRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .message(exception.getMessage())
                .details(request.getDescription(false))
                .build(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(EntityNotFoundException exception, WebRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(new Date())
                .code(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND.toString())
                .message(exception.getMessage())
                .details(request.getDescription(false))
                .build(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidRequestException.class})
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(InvalidRequestException exception, WebRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(new Date())
                .code(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST.toString())
                .message(exception.getMessage())
                .details(request.getDescription(false))
                .build(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ConstraintsViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintsViolationException(ConstraintsViolationException exception, WebRequest request) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(new Date())
                .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.toString())
                .message(exception.getMessage())
                .details(request.getDescription(false))
                .build(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
