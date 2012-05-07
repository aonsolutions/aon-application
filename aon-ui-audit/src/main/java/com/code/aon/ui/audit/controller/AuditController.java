package com.code.aon.ui.audit.controller;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.ui.audit.AuditManager;

/**
 * @author atellitu
 *
 */
public class AuditController {
		
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditController.class);

	private Application application;
	
	/**
	 * Instantiates a new application option controller.
	 */
	public AuditController() {
		init();
	}

	public Application getApplication() {
		return application;
	}

	public Action getAction( String name ) throws ManagerBeanException {
		return AuditManager.getAction(name, application);		
	}
	
	private void init() {
		String context = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
		try {
			this.application = AuditManager.getApplication(context);
			LOGGER.info( "Using {}", application );
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error getting application", e );
		}		
	}
	
}