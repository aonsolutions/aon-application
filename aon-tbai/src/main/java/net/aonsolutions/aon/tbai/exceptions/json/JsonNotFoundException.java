package net.aonsolutions.aon.tbai.exceptions.json;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class JsonNotFoundException extends TbaiException{
	private static final long serialVersionUID = 1L;
	public JsonNotFoundException() {super();}
	public JsonNotFoundException(String message, Throwable cause) {super(message, cause);}
	public JsonNotFoundException(String message) {super(message);}
	public JsonNotFoundException(Throwable cause) {super(cause);}
}
