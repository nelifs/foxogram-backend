package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MFAIsAlreadySetException extends BaseException {

	public MFAIsAlreadySetException() {
		super("MFA is already set.", MFAIsAlreadySetException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.MFA_IS_ALREADY_SET.getValue());
	}
}
