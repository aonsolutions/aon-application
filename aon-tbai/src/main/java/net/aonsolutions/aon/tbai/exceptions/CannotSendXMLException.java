package net.aonsolutions.aon.tbai.exceptions;

public class CannotSendXMLException extends TbaiException{
	public CannotSendXMLException() {}
	public CannotSendXMLException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public CannotSendXMLException(String message, Throwable cause) {super(message, cause);}
	public CannotSendXMLException(String message) {super(message);}
	public CannotSendXMLException(Throwable cause) {super(cause);}
}
