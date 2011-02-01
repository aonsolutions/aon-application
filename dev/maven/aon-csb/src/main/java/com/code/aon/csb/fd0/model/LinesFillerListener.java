package com.code.aon.csb.fd0.model;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Lines filled event listeners interface
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public interface LinesFillerListener extends EventListener {

	/**
	 * Event fired
	 * 
	 * @param event the fired event
	 */
	public void lineFilled(EventObject event);

}
