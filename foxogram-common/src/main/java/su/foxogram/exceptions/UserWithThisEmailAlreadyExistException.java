package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserWithThisEmailAlreadyExistException extends BaseException {

	public UserWithThisEmailAlreadyExistException() {
		super("User with this email already exist", UserWithThisEmailAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.USER_WITH_THIS_EMAIL_ALREADY_EXIST.getValue());
	}
}