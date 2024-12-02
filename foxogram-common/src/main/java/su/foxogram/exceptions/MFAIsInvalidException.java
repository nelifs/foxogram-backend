package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MFAIsInvalidException extends BaseException {

	public MFAIsInvalidException() {
		super("MFA code is invalid.", MFAIsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.MFA_IS_INVALID.getValue());
	}
}
