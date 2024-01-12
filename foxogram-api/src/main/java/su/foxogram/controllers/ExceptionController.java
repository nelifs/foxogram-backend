package su.foxogram.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.foxogram.constructors.RequestMessage;
import su.foxogram.exceptions.*;
import su.foxogram.services.AuthorizationService;

@RestControllerAdvice
public class ExceptionController {

	Logger logger = LoggerFactory.getLogger(ExceptionController.class);
	@ExceptionHandler({BaseException.class})
	public ResponseEntity<String> handleException(BaseException exception) {
		logger.error("EXCEPTION ({}, {}, {}) occurred!\n", exception.getErrorCode(), exception.getStatus(), exception.getMessage());
		return Message(exception);
	}

	private ResponseEntity<String> Message(BaseException exception) {
		return ResponseEntity.status(exception.getStatus()).contentType(MediaType.APPLICATION_JSON).body(new RequestMessage().setSuccess(false).addField("errorCode", String.valueOf(exception.getErrorCode())).addField("message", exception.getMessage()).build());
	}
}
