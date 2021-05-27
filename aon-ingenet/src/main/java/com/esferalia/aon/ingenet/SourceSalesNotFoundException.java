package com.esferalia.aon.ingenet;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;



public class SourceSalesNotFoundException extends AonException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public SourceSalesNotFoundException() {
		super();
	}
	public SourceSalesNotFoundException(String message) {
		super(message);
	}
	public SourceSalesNotFoundException(Throwable cause) {
		super(cause);
	}
	public SourceSalesNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}