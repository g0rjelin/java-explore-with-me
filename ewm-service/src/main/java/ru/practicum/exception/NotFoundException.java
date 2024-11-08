package ru.practicum.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(Long entityId, String entityName) {
        super(String.format("%s with id=%d was not found", entityName, entityId));
    }
}
