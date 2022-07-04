package com.example.insidetesttask.exceptions;

import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionHandlerResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionData onBadCredentialsException(BadCredentialsException exception) {
        return new ExceptionData(exception.getClass().getName(), exception.getMessage());
    }

    @ExceptionHandler(NotCompatibleNameWithTokenSubject.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionData onNotCompatibleNameWithTokenSubjectException(NotCompatibleNameWithTokenSubject exception) {
        return new ExceptionData(exception.getClass().getName(), exception.getMessage());
    }

    @ExceptionHandler(InvalidPropertyStructureException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionData onInvalidPropertyStructureException(InvalidPropertyStructureException exception) {
        return new ExceptionData(exception.getClass().getTypeName(), exception.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionData onJwtExceptionException(JwtException exception) {
        return new ExceptionData(exception.getClass().getTypeName(), exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionData onRuntimeException(RuntimeException exception) {
        return new ExceptionData(exception.getClass().getTypeName(), exception.getMessage());
    }




    @Data
    @AllArgsConstructor
    private static class ExceptionData {
        private String exceptionType;
        private String exceptionMsg;

    }
}
