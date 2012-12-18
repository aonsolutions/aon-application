package com.code.aon.ui.audit.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Application;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

/**
 * @author atellitu
 *
 */
public class AuditController {
		
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditController.class);

	private Application application;

	private boolean showPayrollPortal;
	
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
			initShowPayrollPortal();
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error on init", e );
		}		
	}
	
	public boolean isShowPayrollPortal() {
		return showPayrollPortal;
	}

	private void initShowPayrollPortal() throws ManagerBeanException {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		if ( ds.isChildDomain() ) {
			DomainType parentType = AuditManager.getDomainType(ds.getParentDomainId());
			if ( parentType == DomainType.CONSULTANCY ) {
				this.showPayrollPortal = ! AuditManager.hasModule(ds.getDomainId(), application.getId(), Module.PAYROLL);
			}
		}
	}	
	
}