package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TOTPKeyIsInvalidException extends BaseException {

	public TOTPKeyIsInvalidException() {
		super("TOTP key is invalid.", TOTPKeyIsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.TOTP_KEY_IS_INVALID.getValue());
	}
}
