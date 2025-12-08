package dev.sethan8r.mama.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistException extends BusinessException{
    public AlreadyExistException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
