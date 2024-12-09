package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChannelAlreadyExistException extends BaseException {

	public ChannelAlreadyExistException() {
		super("Channel with this name already exist", ChannelAlreadyExistException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.CHANNEL_ALREADY_EXIST.getValue());
	}
}