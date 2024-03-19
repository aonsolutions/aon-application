package net.aonsolutions.aon.report;


public class AonReportException extends Exception {

	private static final long serialVersionUID = -3730190195705367708L;

    public AonReportException() {
        super();
    }
    public AonReportException(String message) {
        super(message);
    }
    public AonReportException(Throwable cause) {
        super(cause);
    }
    public AonReportException(String message, Throwable cause) {
        super(message, cause);
    }
  
}