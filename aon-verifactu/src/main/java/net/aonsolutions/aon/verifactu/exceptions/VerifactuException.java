package net.aonsolutions.aon.verifactu.exceptions;

import java.io.Serializable;

public class VerifactuException extends Exception  implements Serializable{

	private static final long serialVersionUID = 1088764044366567246L;
	
	private final VerifactuError verifactuError;
	
	public VerifactuException() {
		this(VerifactuError.AON_9000); 
	}
	public VerifactuException(Throwable cause) {
		this(VerifactuError.AON_9000, cause);
	}
	public VerifactuException(VerifactuError error, Throwable cause) {
		super(error.getMessage(), cause);
		this.verifactuError = error;
	}
	public VerifactuException(VerifactuError error) {
		super(error.getMessage());
		this.verifactuError = error;
	}
	
	public VerifactuError getVerifactuError() {
		return verifactuError;
	}
	
}
