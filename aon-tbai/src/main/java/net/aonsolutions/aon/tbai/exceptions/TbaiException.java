package net.aonsolutions.aon.tbai.exceptions;

public class TbaiException extends Exception{

	public TbaiException() {super();}
	public TbaiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TbaiException(String message, Throwable cause) {super(message, cause);}
	public TbaiException(String message) {super(message);}
	public TbaiException(Throwable cause) {super(cause);}
}
