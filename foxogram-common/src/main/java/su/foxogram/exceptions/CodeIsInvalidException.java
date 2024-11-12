package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CodeIsInvalidException extends BaseException {

	public CodeIsInvalidException() {
		super("Code is invalid!", CodeIsInvalidException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsEnum.Codes.CODE_IS_INVALID.getValue());
	}
}