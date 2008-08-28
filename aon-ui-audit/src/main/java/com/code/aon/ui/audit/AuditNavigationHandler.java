package com.code.aon.ui.audit;

import java.util.logging.Logger;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

public class AuditNavigationHandler extends NavigationHandler {

	/** Obtiene un logger apropiado. */
	private static final Logger LOGGER = Logger
			.getLogger(AuditNavigationHandler.class.getName());	
	
	private NavigationHandler _base;
	
	public AuditNavigationHandler(NavigationHandler base) {
		_base = base;
	}	
	
	@Override
	public void handleNavigation(FacesContext fc, String actionMethodCurrent, String actionNameCurrent) {
		LOGGER.info( fc.getViewRoot().getViewId() + " - " + actionMethodCurrent + " - " + actionNameCurrent );
		_base.handleNavigation(fc, actionMethodCurrent, actionNameCurrent);
	}

	

}
