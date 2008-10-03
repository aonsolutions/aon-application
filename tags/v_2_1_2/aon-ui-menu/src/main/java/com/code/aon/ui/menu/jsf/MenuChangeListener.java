package com.code.aon.ui.menu.jsf;

import javax.faces.event.AbortProcessingException;

/**
 * A listener interface for receiving {@link MenuChangeEvent}s. A class that
 * is interested in receiving such events implements this interface,
 * and then registers itself with the source {@link com.code.aon.ui.menu.IMenuItem}
 * of interest, by calling addMenuChangeListener().
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-oct-2005
 * @since 1.0
 */
public interface MenuChangeListener {

	/**
	 * Invoked when the current menu is changed
	 * 
	 * @param event
	 * 			a {@link MenuChangeEvent} object describing the event source.
	 * @throws AbortProcessingException
	 * 			Signal the JavaServer Faces implementation that no further
	 * 			processing on the current event should be performed.
	 */
	void menuChanged(MenuChangeEvent event) throws AbortProcessingException;
	
}
