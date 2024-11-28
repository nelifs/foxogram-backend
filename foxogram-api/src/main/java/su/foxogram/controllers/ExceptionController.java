package su.foxogram.controllers;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.foxogram.dtos.response.ExceptionDTO;
import su.foxogram.exceptions.BaseException;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class ExceptionController {
	@ExceptionHandler({ BaseException.class })
	public ResponseEntity<ExceptionDTO> handleBaseException(BaseException exception) {
		log.error("CLIENT (USER) EXCEPTION ({}, {}, {}) occurred!\n", exception.getErrorCode(), exception.getStatus(), exception.getMessage());
		return ResponseEntity.status(exception.getStatus()).body(new ExceptionDTO(false, exception.getErrorCode(), exception.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionDTO> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		String message = "Request body cannot be empty.";
		log.error("SERVER REQUEST EXCEPTION ({}, {}, {}) occurred!\n", 999, HttpStatus.INTERNAL_SERVER_ERROR, message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDTO(false, 999, message));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ExceptionDTO> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
		String message = "User with this username/email already exist.";
		log.error("SERVER REQUEST EXCEPTION ({}, {}, {}) occurred!\n", 999, HttpStatus.INTERNAL_SERVER_ERROR, message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDTO(false, 999, message));
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<ExceptionDTO> handleValidationException(MethodArgumentNotValidException exception) {
		List<String> messagesArray = new ArrayList<>();
		exception.getBindingResult().getAllErrors().forEach((error) -> messagesArray.add(error.getDefaultMessage()));

		String message = String.join(", ", messagesArray);
		log.error("SERVER EXCEPTION ({}, {}, {}) occurred!\n", 999, HttpStatus.INTERNAL_SERVER_ERROR, message);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDTO(false, 1001, message));
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ExceptionDTO> handleException(Exception exception) {
		log.error("SERVER EXCEPTION ({}, {}, {}) occurred!\n", 999, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDTO(false, 999, exception.getMessage()));
	}
}
