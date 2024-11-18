package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChannelNotFoundException extends BaseException {

	public ChannelNotFoundException() {
		super("Unknown channel", ChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.CHANNEL_NOT_FOUND.getValue());
	}
}