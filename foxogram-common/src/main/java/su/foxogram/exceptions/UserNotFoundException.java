package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BaseException {

	public UserNotFoundException() {
		super("Unknown user.", UserNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.USER_NOT_FOUND.getValue());
	}
}