package com.code.aon.db;

public class EntityProcessException extends Exception {

	private static final long serialVersionUID = -2065541219807778133L;

	public EntityProcessException() {
        super();
    }

    public EntityProcessException(String message) {
        super(message);
    }

    public EntityProcessException(Throwable cause) {
        super(cause);
    }

    public EntityProcessException(String message, Throwable cause) {
        super(message, cause);
    }


}
