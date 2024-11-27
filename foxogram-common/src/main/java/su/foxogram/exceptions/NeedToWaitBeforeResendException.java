package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NeedToWaitBeforeResendException extends BaseException {

	public NeedToWaitBeforeResendException() {
		super("You need to wait 1 minute to resend code again", NeedToWaitBeforeResendException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.NEED_TO_WAIT_BEFORE_RESEND.getValue());
	}
}