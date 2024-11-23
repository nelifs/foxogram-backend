package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeExpiredException extends BaseException {

	public CodeExpiredException() {
		super("Code has expired!", CodeExpiredException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.CODE_EXPIRED.getValue());
	}
}