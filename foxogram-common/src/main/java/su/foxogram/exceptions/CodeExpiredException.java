package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeExpiredException extends BaseException {

	public CodeExpiredException() {
		super("Code has expired!", CodeExpiredException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsEnum.Codes.CODE_IS_INVALID.getValue());
	}
}