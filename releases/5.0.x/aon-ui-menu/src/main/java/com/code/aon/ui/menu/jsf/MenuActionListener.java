package com.code.aon.ui.menu.jsf;

import javax.faces.event.AbortProcessingException;

/**
 * A listener interface for receiving {@link MenuEvent}s. A class that
 * is interested in receiving such events implements this interface,
 * and then registers itself with the source {@link com.code.aon.ui.menu.IMenuItem}
 * of interest, by calling addActionListener().
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-oct-2005
 * @since 1.0
 */
public interface MenuActionListener {

	/**
	 * Invoked when the action described by the specified {@link MenuEvent} occurs.
	 * 
	 * @param menuEvent
	 * 			a {@link MenuEvent} object describing the event source.
	 * @throws AbortProcessingException
	 * 			Signal the JavaServer Faces implementation that no further
	 * 			processing on the current event should be performed.
	 */
	void processAction(MenuEvent menuEvent) throws AbortProcessingException;
	
}
