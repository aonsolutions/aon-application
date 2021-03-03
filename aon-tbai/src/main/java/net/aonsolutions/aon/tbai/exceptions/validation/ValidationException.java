package net.aonsolutions.aon.tbai.exceptions.validation;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class ValidationException extends TbaiException{
	public ValidationException() {super();}
	public ValidationException(String message, Throwable cause) {super(message, cause);}
	public ValidationException(String message) {super(message);}
	public ValidationException(Throwable cause) {super(cause);}
}
