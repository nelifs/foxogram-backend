package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import su.foxogram.constants.ExceptionsConstants;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UploadFailedException extends BaseException {

	public UploadFailedException() {
		super("Image upload failed", UploadFailedException.class.getAnnotation(ResponseStatus.class).value(), ExceptionsConstants.Codes.UPLOAD_FAILED.getValue());
	}
}