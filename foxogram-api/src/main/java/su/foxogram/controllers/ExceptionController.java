package su.foxogram.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.foxogram.dtos.response.ExceptionDTO;
import su.foxogram.exceptions.BaseException;

@RestControllerAdvice
public class ExceptionController {

	final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

	@ExceptionHandler({ BaseException.class })
	public ResponseEntity<ExceptionDTO> handleBaseException(BaseException exception) {
		logger.error("CLIENT (USER) EXCEPTION ({}, {}, {}) occurred!\n", exception.getErrorCode(), exception.getStatus(), exception.getMessage());
		return ResponseEntity.status(exception.getStatus()).body(new ExceptionDTO(false, exception.getErrorCode(), exception.getMessage()));
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ExceptionDTO> handleException(Exception exception) {
		logger.error("SERVER EXCEPTION ({}, {}, {}) occurred!\n", 999, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionDTO(false, 999, exception.getMessage()));
	}
}
