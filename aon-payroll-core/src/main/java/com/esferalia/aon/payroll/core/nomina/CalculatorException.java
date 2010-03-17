package com.esferalia.aon.payroll.core.nomina;

public class CalculatorException extends Exception {

	private static final long serialVersionUID = -5386401441718903779L;

    public CalculatorException() {
        super();
    }

    public CalculatorException(String message) {
        super(message);
    }

    public CalculatorException(Throwable cause) {
        super(cause);
    }

    public CalculatorException(String message, Throwable cause) {
        super(message, cause);
    }

}