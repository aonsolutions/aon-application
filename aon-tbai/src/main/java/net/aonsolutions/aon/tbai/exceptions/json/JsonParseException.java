package net.aonsolutions.aon.tbai.exceptions.json;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class JsonParseException extends TbaiException{
	public JsonParseException() {super();}
	public JsonParseException(String message, Throwable cause) {super(message, cause);}
	public JsonParseException(String message) {super(message);}
	public JsonParseException(Throwable cause) {super(cause);}	
}
