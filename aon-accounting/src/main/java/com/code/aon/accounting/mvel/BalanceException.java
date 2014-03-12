package com.code.aon.accounting.mvel;

import com.code.aon.common.AonVersion;

public class BalanceException extends Exception {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public BalanceException() {
        super();
    }
    public BalanceException(String message) {
        super(message);
    }
    public BalanceException(Throwable cause) {
        super(cause);
    }
    public BalanceException(String message, Throwable cause) {
        super(message, cause);
    }

}