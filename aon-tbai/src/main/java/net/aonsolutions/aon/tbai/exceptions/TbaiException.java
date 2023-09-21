package net.aonsolutions.aon.tbai.exceptions;

import java.io.Serializable;

public class TbaiException extends Exception  implements Serializable{

	private static final long serialVersionUID = 1088764044366567246L;
	
	private TBAIError tbaiError;
	
	public TbaiException() {super();}
	public TbaiException(String message, Throwable cause) {super(message, cause);}
	public TbaiException(String message) {super(message);}
	public TbaiException(Throwable cause) {super(cause);}
	
	public TbaiException(TBAIError error) {
		super(error.getMessage());
		this.tbaiError = error;
	}
	
	public TBAIError getTbaiError() {
		return tbaiError;
	}
	
}
