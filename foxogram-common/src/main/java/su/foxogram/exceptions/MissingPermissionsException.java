package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MissingPermissionsException extends BaseException {

	public MissingPermissionsException() {
		super("You don't have enough permissions to delete this channel", MissingPermissionsException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsEnum.Codes.MISSING_PERMISSIONS.getValue());
	}
}