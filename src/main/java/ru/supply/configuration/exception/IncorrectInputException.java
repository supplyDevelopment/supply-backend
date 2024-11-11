package ru.supply.configuration.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IncorrectInputException extends RuntimeException {
    public IncorrectInputException(String message) {
        super(message);
        log.error(message);
    }
}
