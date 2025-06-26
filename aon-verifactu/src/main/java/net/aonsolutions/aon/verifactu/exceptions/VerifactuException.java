package net.aonsolutions.aon.verifactu.exceptions;

import java.io.Serializable;

public class VerifactuException extends Exception  implements Serializable{

	private static final long serialVersionUID = 1088764044366567246L;
	
	private VerifactuError verifactuError;
	
	public VerifactuException() {super();}
	public VerifactuException(String message, Throwable cause) {super(message, cause);}
	public VerifactuException(String message) {super(message);}
	public VerifactuException(Throwable cause) {super(cause);}
	
	public VerifactuException(VerifactuError error) {
		super(error.getMessage());
		this.verifactuError = error;
	}
	
	public VerifactuError getVerifactuError() {
		return verifactuError;
	}
	
}
