package com.franzoia.productservice.config;

import com.franzoia.common.config.AbstractApplicationExceptionHandler;
import com.franzoia.common.exception.ConstraintsViolationException;
import com.franzoia.common.exception.EntityNotFoundException;
import com.franzoia.common.exception.InvalidRequestException;
import com.franzoia.common.exception.ServiceNotAvailableException;
import com.franzoia.common.util.ErrorResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class ApplicationExceptionHandler extends AbstractApplicationExceptionHandler {

    @ExceptionHandler({ServiceNotAvailableException.class})
    public ResponseEntity<ErrorResponse> handleProductServiceNotAvailableException(ServiceNotAvailableException exception, WebRequest request) {
        return getResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), exception, request);
    }

    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(EntityNotFoundException exception, WebRequest request) {
        return getResponse(HttpStatus.NOT_FOUND.value(), exception, request);
    }

    @ExceptionHandler({InvalidRequestException.class})
    public ResponseEntity<ErrorResponse> handleProductNotFoundException(InvalidRequestException exception, WebRequest request) {
        return getResponse(HttpStatus.BAD_REQUEST.value(), exception, request);
    }

    @ExceptionHandler({ConstraintsViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintsViolationException(ConstraintsViolationException exception, WebRequest request) {
        return getResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), exception, request);
    }

    @ExceptionHandler({FeignException.class})
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException exception, WebRequest request) {
        return getResponse(exception.status(), exception, request);
    }

}
