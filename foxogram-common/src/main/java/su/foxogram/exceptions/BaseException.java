package su.foxogram.exceptions;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends Exception {

	public int errorCode;
	public HttpStatus status;
	public String message;

	public BaseException(String message, HttpStatus status, int errorCode) {
		this.message = message;
		this.status = status;
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
