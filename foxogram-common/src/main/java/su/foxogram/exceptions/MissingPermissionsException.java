package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class MissingPermissionsException extends BaseException {

	public MissingPermissionsException() {
		super("You don't have enough permissions to perform this action", MissingPermissionsException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.MISSING_PERMISSIONS.getValue());
	}
}