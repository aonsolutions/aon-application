package com.code.aon.dbdumper.event;

import java.util.EventObject;

/**
 * Event generated in the database dump or recover
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class DumpEvent extends EventObject  {

	/**
	 * The message of the event 
	 */
	private String message;

	/**
	 * Constructor
	 * 
	 * @param source the object linked to the event
	 * @param message the message explaining the success
	 */
	public DumpEvent ( Object source, String message ) {
		super ( source );
		this.message = message;
	}
	
	/**
	 * Returns the message
	 * 
	 * @return message
	 */
	public String getMessage(){
		return message;
	}
}