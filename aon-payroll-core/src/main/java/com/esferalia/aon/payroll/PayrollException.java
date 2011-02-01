package com.esferalia.aon.payroll;

public class PayrollException extends Exception {

	private static final long serialVersionUID = -5386401441718903779L;

    public PayrollException() {
        super();
    }

    public PayrollException(String message) {
        super(message);
    }

    public PayrollException(Throwable cause) {
        super(cause);
    }

    public PayrollException(String message, Throwable cause) {
        super(message, cause);
    }

}