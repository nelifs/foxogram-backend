package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MessageNotFoundException extends BaseException {

	public MessageNotFoundException() {
		super("Unable to find message(s) for this channel or matching these parameters", ExceptionsEnum.Codes.MESSAGE_NOT_FOUND.getValue());
	}
}