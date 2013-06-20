package com.esferalia.aon.file.payroll.contract.pdf;

import com.code.aon.common.AonException;

public class UnsupportedContractDocumentException extends AonException {

	private static final long serialVersionUID = 2658939739517432990L;
	
	public UnsupportedContractDocumentException() {
		super();
	}
	public UnsupportedContractDocumentException(String message) {
		super(message);
	}
	public UnsupportedContractDocumentException(Throwable cause) {
		super(cause);
	}
	public UnsupportedContractDocumentException(String message, Throwable cause) {
		super(message, cause);
	}

}
