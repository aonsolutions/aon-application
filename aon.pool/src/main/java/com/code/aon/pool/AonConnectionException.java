package net.aonsolutions.core.pool;

public class AonConnectionException extends Exception {

	private static final long serialVersionUID = -5386401441718903779L;

    public AonConnectionException() {
        super();
    }

    public AonConnectionException(String message) {
        super(message);
    }

    public AonConnectionException(Throwable cause) {
        super(cause);
    }

    public AonConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

}