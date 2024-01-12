package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.enums.ExceptionsEnum;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberInChannelNotFoundException extends BaseException {

	public MemberInChannelNotFoundException() {
		super("Can't find member in this channel", MemberInChannelNotFoundException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsEnum.Codes.MEMBER_IN_CHANNEL_NOT_FOUND.getValue());
	}
}