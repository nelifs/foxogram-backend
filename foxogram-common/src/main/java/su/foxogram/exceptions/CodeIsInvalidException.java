package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeIsInvalidException extends BaseException {

	public CodeIsInvalidException() {
		super("Code is invalid!", CodeIsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.CODE_IS_INVALID.getValue());
	}
}