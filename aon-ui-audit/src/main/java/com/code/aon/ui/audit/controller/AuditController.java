package com.code.aon.ui.audit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.util.AonUtil;

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
		try {
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();			
			IManagerBean bean = BeanManager.getManagerBean(Application.class);
			this.application = (Application) bean.get(appId);
			LOGGER.info( "Using {}", application );
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error on init", e );
		}		
	}
	
}