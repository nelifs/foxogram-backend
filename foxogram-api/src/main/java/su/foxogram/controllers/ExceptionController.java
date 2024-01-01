package su.foxogram.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import su.foxogram.constructors.RequestMessage;
import su.foxogram.exceptions.*;

@RestControllerAdvice
public class ExceptionController {
	@ExceptionHandler({MessageNotFoundException.class, UserNotFoundException.class, ChannelNotFoundException.class, MemberInChannelNotFoundException.class})
	public ResponseEntity<String> handleNotFoundException(Exception e) {
		return Message(HttpStatus.NOT_FOUND, e);
	}

	@ExceptionHandler({UserAuthenticationNeededException.class, UserEmailNotVerifiedException.class})
	public ResponseEntity<String> handleAuthenticationNeededException(Exception e) {
		return Message(HttpStatus.FORBIDDEN, e);
	}

	private ResponseEntity<String> Message(HttpStatus status, Exception e) {
		return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(new RequestMessage().setSuccess(false).addField("message", e.getMessage()).build());
	}
}
