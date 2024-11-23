package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberAlreadyInChannelException extends BaseException {

	public MemberAlreadyInChannelException() {
		super("You've already joined this channel.", MemberAlreadyInChannelException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.MEMBER_ALREADY_IN_CHANNEL.getValue());
	}
}