package com.esferalia.aon.file.payroll.contract.pdf;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;

public class UnsupportedContractDocumentException extends AonException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
