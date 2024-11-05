package ru.practicum.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UniqueConstraintException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BadRequestException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleBadRequest(final Exception e) {
        return ApiError.builder()
                .errors(List.of(e.getStackTrace()))
                .message(e.getMessage())
                .reason(e.getLocalizedMessage())
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler({ConflictException.class, UniqueConstraintException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    ApiError handleConflict(final Exception e) {
        return ApiError.builder()
                .errors(List.of(e.getStackTrace()))
                .message(e.getMessage())
                .reason(e.getLocalizedMessage())
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError handleNotFound(final NotFoundException e) {
        return ApiError.builder()
                .errors(List.of(e.getStackTrace()))
                .message(e.getMessage())
                .reason(e.getLocalizedMessage())
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ApiError handleException(final RuntimeException e) {
        log.error("Internal server error: {}", e.getMessage());
        return ApiError.builder()
                .errors(List.of(e.getStackTrace()))
                .message(e.getMessage())
                .reason(e.getLocalizedMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .timestamp(Instant.now())
                .build();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Builder
    @Getter
    private static class ApiError {
        final List<StackTraceElement> errors;
        final String status;
        final String reason;
        final String message;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        final Instant timestamp;
    }
}
