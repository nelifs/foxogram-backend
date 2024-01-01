package su.foxogram.exceptions;

public abstract class BaseException extends Exception {

	public int errorCode;
	public String message;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BaseException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
