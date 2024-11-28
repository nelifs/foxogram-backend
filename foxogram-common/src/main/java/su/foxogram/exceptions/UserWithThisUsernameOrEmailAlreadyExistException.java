package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserWithThisUsernameOrEmailAlreadyExistException extends BaseException {

	public UserWithThisUsernameOrEmailAlreadyExistException() {
		super("User with this username/email already exist.", UserWithThisUsernameOrEmailAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.USER_WITH_THIS_USERNAME_OR_EMAIL_ALREADY_EXIST.getValue());
	}
}