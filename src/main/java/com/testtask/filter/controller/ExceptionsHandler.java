package com.testtask.filter.controller;

import com.testtask.filter.data.dto.ErrorDto;
import javax.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ServerWebInputException;

@Log4j2
@ControllerAdvice
public class ExceptionsHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorDto handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("ERROR:\n" + ex.getMessage(), ex);
        return new ErrorDto(ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpClientErrorException.class)
    public ErrorDto handleHttpClientErrorException(HttpClientErrorException ex) {
        log.error("ERROR:\n" + ex.getMessage(), ex);
        return new ErrorDto(ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDto handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ERROR:\n" + ex.getMessage(), ex);
        return new ErrorDto(ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ServerWebInputException.class)
    public ErrorDto handleServerWebInputException(ServerWebInputException ex) {
        log.error("ERROR:\n" + ex.getMessage(), ex);
        return new ErrorDto(ex.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleExceptionInternal(Exception ex) {
        log.error("ERROR:\n" + ex.getMessage(), ex);
        return new ErrorDto(ex.getMessage());
    }
}
