package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserAuthenticationNeededException extends BaseException {

	public UserAuthenticationNeededException() {
		super("You need to authenticate to perform this action", ExceptionsEnum.Codes.USER_AUTHENTICATION_NEEDED.getValue());
	}
}