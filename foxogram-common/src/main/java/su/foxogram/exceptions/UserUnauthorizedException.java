package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UserUnauthorizedException extends BaseException {

	public UserUnauthorizedException() {
		super("You need to authorize first.", UserUnauthorizedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsEnum.Codes.USER_NOT_FOUND.getValue());
	}
}