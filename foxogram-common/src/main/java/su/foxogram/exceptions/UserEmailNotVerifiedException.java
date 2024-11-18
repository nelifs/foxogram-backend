package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserEmailNotVerifiedException extends BaseException {

	public UserEmailNotVerifiedException() {
		super("You need to verify your email first", UserEmailNotVerifiedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.USER_EMAIL_NOT_VERIFIED.getValue());
	}
}