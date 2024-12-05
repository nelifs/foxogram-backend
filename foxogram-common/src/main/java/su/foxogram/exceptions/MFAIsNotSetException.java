package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MFAIsNotSetException extends BaseException {

	public MFAIsNotSetException() {
		super("MFA is not set.", MFAIsNotSetException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.MFA_IS_NOT_SET.getValue());
	}
}
