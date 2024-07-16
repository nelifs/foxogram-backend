package su.foxogram.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.foxogram.constructors.RequestMessage;
import su.foxogram.exceptions.BaseException;

@RestControllerAdvice
public class ExceptionController {

	Logger logger = LoggerFactory.getLogger(ExceptionController.class);

	@ExceptionHandler({ BaseException.class })
	public ResponseEntity<String> handleBaseException(BaseException exception) {
		logger.error("CLIENT (USER) EXCEPTION ({}, {}, {}) occurred!\n", exception.getErrorCode(), exception.getStatus(), exception.getMessage());
		return Message(String.valueOf(exception.getErrorCode()), exception.getStatus(), exception.getMessage());
	}

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<String> handleException(Exception exception) {
		logger.error("SERVER EXCEPTION ({}, {}, {}) occurred!\n", "000", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
		return Message("000", HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
	}

	private ResponseEntity<String> Message(String errorCode, HttpStatus status, String message) {
		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(new RequestMessage().setSuccess(false).addField("errorCode", errorCode).addField("message", message).build());
	}
}
