package com.esferalia.aon.watson;

import java.text.MessageFormat;

public class AonCoreException extends RuntimeException {

	private static final long serialVersionUID = -9000345934049079813L;
	
	private AonError errorCode;
	private String argumentsMessage;
	
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
    public AonCoreException(AonError errorCode) {
        super();
        this.errorCode = errorCode;
    }
    public AonCoreException(String message,AonError errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    public AonCoreException(Throwable cause,AonError errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }
    public AonCoreException(String message, Throwable cause,AonError errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public AonCoreException(AonError errorCode,Object...arguments) {
        super();
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
    public AonCoreException(String message,AonError errorCode,Object...arguments) {
        super(message);
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
    public AonCoreException(Throwable cause,AonError errorCode,Object...arguments) {
        super(cause);
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
    public AonCoreException(String message, Throwable cause,AonError errorCode,Object...arguments) {
        super(message, cause);
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
/*    
    public String getAonMessage() {
        return this.errorCode == null
        		?super.getMessage()
        		:this.argumentsMessage== null
        			?errorCode.getMessage()
        			:this.argumentsMessage; 
	}
*/    
    public String getMessage() {
        return this.errorCode == null
        		?super.getMessage()
        		:this.argumentsMessage== null
        			?errorCode.getMessage()
        			:this.argumentsMessage; 
	}
    
}