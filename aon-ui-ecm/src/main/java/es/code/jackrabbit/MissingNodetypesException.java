package es.code.jackrabbit;

import es.code.repository.core.BaseRuntimeException;

public class MissingNodetypesException extends BaseRuntimeException {

	/**
	 * Stable serialVersionUID.
	 */
	private static final long serialVersionUID = 222L;

	/**
	 * @param message
	 */
	public MissingNodetypesException() {
		super( "No nodetype configuration file found" );
	}

}
