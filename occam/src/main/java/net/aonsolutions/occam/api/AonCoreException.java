package net.aonsolutions.occam.api;


public class AonCoreException extends RuntimeException {

	private static final long serialVersionUID = -3730190195705367708L;

    public AonCoreException() {
        super();
    }
    public AonCoreException(String message) {
        super(message);
    }
    public AonCoreException(Throwable cause) {
        super(cause);
    }
    public AonCoreException(String message, Throwable cause) {
        super(message, cause);
    }
  
}