package com.code.aon.accounting.mvel;

public class BalanceException extends Exception {
	
	private static final long serialVersionUID = -8652509831054907089L;
	
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