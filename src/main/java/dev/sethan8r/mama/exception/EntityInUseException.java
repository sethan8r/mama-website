package dev.sethan8r.mama.exception;

import org.springframework.http.HttpStatus;

public class EntityInUseException extends BusinessException{
    public EntityInUseException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
