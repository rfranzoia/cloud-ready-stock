package com.franzoia.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.franzoia.common.util.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.Optional;

@Slf4j
public class AbstractApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    protected ResponseEntity<ErrorResponse> getResponse(Integer statusCode, Exception exception, WebRequest request) {
        log.info("message from service: {}", getExceptionMessage(exception).toString());
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(new Date())
                .code(statusCode)
                .status(HttpStatus.valueOf(statusCode).toString())
                .message(getExceptionMessage(exception).get().getMessage())
                .details(request.getDescription(false))
                .build(), HttpStatus.valueOf(statusCode));
    }

    protected Optional<ErrorResponse> getExceptionMessage(Exception ex) {
        try {
            String[] messages = ex.getMessage().split("\\[");
            ObjectMapper mapper = new ObjectMapper();
            return Optional.of(mapper.readValue(messages[messages.length - 1], ErrorResponse.class));
        } catch (JsonProcessingException ignored) {
            return Optional.of(ErrorResponse.builder()
                    .message(ex.getMessage())
                    .build());
        }
    }

}
