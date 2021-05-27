package net.aonsolutions.aon.tbai.exceptions;

public class TbaiException extends Exception{

	private static final long serialVersionUID = 1088764044366567246L;
	
	public TbaiException() {super();}
	public TbaiException(String message, Throwable cause) {super(message, cause);}
	public TbaiException(String message) {super(message);}
	public TbaiException(Throwable cause) {super(cause);}
}
