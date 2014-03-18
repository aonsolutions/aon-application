package com.code.aon.ui.accounting.check;

import com.code.aon.common.AonException;
import com.code.aon.AonVersion;

public class AonCheckException extends AonException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public AonCheckException() {
		super();
	}

	public AonCheckException(String message) {
		super(message);
	}

	public AonCheckException(Throwable cause) {
		super(cause);
	}

	public AonCheckException(String message, Throwable cause) {
		super(message, cause);
	}

}
