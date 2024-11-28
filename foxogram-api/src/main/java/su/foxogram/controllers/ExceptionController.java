package su.foxogram.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.foxogram.dtos.response.ExceptionDTO;
import su.foxogram.exceptions.BaseException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    private ResponseEntity<ExceptionDTO> buildErrorResponse(int errorCode, String message, HttpStatus status) {
        log.error("SERVER EXCEPTION ({}, {}, {}) occurred!\n", errorCode, status, message);
        return ResponseEntity.status(status).body(new ExceptionDTO(false, errorCode, message));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionDTO> handleBaseException(BaseException exception) {
        log.error("CLIENT (USER) EXCEPTION ({}, {}, {}) occurred!\n", exception.getErrorCode(), exception.getStatus(), exception.getMessage());
        return buildErrorResponse(exception.getErrorCode(), exception.getMessage(), exception.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        return buildErrorResponse(999, "Request body cannot be empty.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDTO> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        return buildErrorResponse(999, "User with this username/email already exist.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return buildErrorResponse(1001, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception exception) {
        return buildErrorResponse(999, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
