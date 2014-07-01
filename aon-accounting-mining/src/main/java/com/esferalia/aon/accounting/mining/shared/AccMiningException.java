package com.esferalia.aon.accounting.mining.shared;

public class AccMiningException extends Exception {
	
	private static final long serialVersionUID = -8652509831054907089L;
	
	public AccMiningException() {
        super();
    }
    public AccMiningException(String message) {
        super(message);
    }
    public AccMiningException(Throwable cause) {
        super(cause);
    }
    public AccMiningException(String message, Throwable cause) {
        super(message, cause);
    }

}