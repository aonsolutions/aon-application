package com.esferalia.aon.master.impl.server.sql;

import java.text.MessageFormat;

import com.code.aon.AonVersion;

public class AonDAOException extends RuntimeException {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private AonDAOError errorCode;
	private String argumentsMessage;
	
    public AonDAOException() {
        super();
    }
    public AonDAOException(String message) {
        super(message);
    }
    public AonDAOException(Throwable cause) {
        super(cause);
    }
    public AonDAOException(String message, Throwable cause) {
        super(message, cause);
    }
    public AonDAOException(AonDAOError errorCode) {
        super();
        this.errorCode = errorCode;
    }
    public AonDAOException(String message,AonDAOError errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    public AonDAOException(Throwable cause,AonDAOError errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }
    public AonDAOException(String message, Throwable cause,AonDAOError errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public AonDAOException(AonDAOError errorCode,Object...arguments) {
        super();
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
    public AonDAOException(String message,AonDAOError errorCode,Object...arguments) {
        super(message);
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
    public AonDAOException(Throwable cause,AonDAOError errorCode,Object...arguments) {
        super(cause);
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
    public AonDAOException(String message, Throwable cause,AonDAOError errorCode,Object...arguments) {
        super(message, cause);
        this.errorCode = errorCode;
        this.argumentsMessage = MessageFormat.format(errorCode.getMessage(), arguments);
    }
    
    public String getAonMessage() {
        return this.errorCode == null
        		?this.getMessage()
        		:this.argumentsMessage== null
        			?errorCode.getMessage()
        			:this.argumentsMessage; 
	}
    
    public String getMessage() {
        return this.errorCode == null
        		?this.getMessage()
        		:this.argumentsMessage== null
        			?errorCode.getMessage()
        			:this.argumentsMessage; 
	}
    
}