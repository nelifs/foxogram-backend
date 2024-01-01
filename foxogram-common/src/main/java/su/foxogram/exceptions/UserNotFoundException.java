package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotFoundException extends BaseException {

	public UserNotFoundException() {
		super("Unknown user", ExceptionsEnum.Codes.USER_NOT_FOUND.getValue());
	}
}