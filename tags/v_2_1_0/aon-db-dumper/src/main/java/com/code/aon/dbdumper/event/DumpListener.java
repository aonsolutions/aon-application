package com.code.aon.dbdumper.event;

/**
 * This is the interface of event listeners 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public interface DumpListener {
	
	/**
	 * And event happened
	 * 
	 * @param event the event happened
	 */
	public void eventHappen ( DumpEvent event );

}