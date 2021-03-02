package net.aonsolutions.aon.tbai.exceptions;

public class CannotCreateXMLException extends TbaiException{
	
	public CannotCreateXMLException() {}
	public CannotCreateXMLException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public CannotCreateXMLException(String message, Throwable cause) {super(message, cause);}
	public CannotCreateXMLException(String message) {super(message);}
	public CannotCreateXMLException(Throwable cause) {super(cause);}
	
}
