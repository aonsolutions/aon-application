package net.aonsolutions.aon.api.error;

public class AonApiException extends RuntimeException {

	private static final long serialVersionUID = -3730190195705367708L;

    public AonApiException() {
        super();
    }
    
    public AonApiException(String message) {
        super(message);
    }
    
    public AonApiException(Throwable cause) {
        super(cause);
    }
    
    public AonApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
