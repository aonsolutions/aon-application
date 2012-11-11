package com.esferalia.aon.file.payroll.contract.pdf.model;

import com.code.aon.common.AonException;

public class UnsupportedContractModelException extends AonException {

	private static final long serialVersionUID = 2658939739517432990L;
	
	public UnsupportedContractModelException() {
		super();
	}
	public UnsupportedContractModelException(String message) {
		super(message);
	}
	public UnsupportedContractModelException(Throwable cause) {
		super(cause);
	}
	public UnsupportedContractModelException(String message, Throwable cause) {
		super(message, cause);
	}

}
