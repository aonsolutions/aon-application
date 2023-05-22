/**
 * 
 */
package com.code.aon.ui.common.servlet;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The listener interface for receiving basicSession events.
 * The class that is interested in processing a basicSession
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addBasicSessionListener<code> method. When
 * the basicSession event occurs, that object's appropriate
 * method is invoked.
 *
 */
public class BasicSessionListener implements HttpSessionListener {

	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(BasicSessionListener.class);
	
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		String context = session.getServletContext().getContextPath();
		LOGGER.info( "Session Created: {} {}", context, session.getId() );
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		String context = session.getServletContext().getContextPath();
		LOGGER.info( "Session Destroyed: {} {}", context, session.getId() );
	}

}
