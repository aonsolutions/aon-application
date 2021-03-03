package net.aonsolutions.aon.tbai.exceptions.json;

import net.aonsolutions.aon.tbai.exceptions.TbaiException;

public class NoJsonFound extends TbaiException{
	public NoJsonFound() {super();}
	public NoJsonFound(String message, Throwable cause) {super(message, cause);}
	public NoJsonFound(String message) {super(message);}
	public NoJsonFound(Throwable cause) {super(cause);}
}
