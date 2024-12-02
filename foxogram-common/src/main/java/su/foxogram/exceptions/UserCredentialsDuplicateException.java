package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserCredentialsDuplicateException extends BaseException {

	public UserCredentialsDuplicateException() {
		super("User with this username/email already exist.", UserCredentialsDuplicateException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.USER_CREDENTIALS_DUPLICATE.getValue());
	}
}
