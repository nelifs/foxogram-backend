package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserCredentialsIsInvalidException extends BaseException {

	public UserCredentialsIsInvalidException() {
		super("Invalid password or email", UserCredentialsIsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.USER_CREDENTIALS_IS_INVALID.getValue());
	}
}