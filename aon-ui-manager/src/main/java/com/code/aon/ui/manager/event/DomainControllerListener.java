package com.code.aon.ui.manager.event;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.Domain;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.UserType;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class DomainControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainControllerListener.class);

	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = domainController.getDomain();
		try {
			domainController.initAccessPolicy();
			if ( getManager().getUserType() == UserType.PARENT ) {
				domain.setParentDomain( getManager().getCurrentDomain() );
			}
			domainController.updateParentDomains();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		Domain domain = domainController.getDomain();
		try {
			updateDomain(domain);
			domainController.insertOrUpdateAccessPolicy();
			DBConnnection dbc = domainController.createAndRegister(domain);
			domainController.registerApplication(AON_DESKTOP, dbc);
			domainController.registerApplication(AON_WEBMAIL, null);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			domainController.removeDomain( domain );
			throw new ControllerListenerException( e.getMessage(), e );
		}
		getManager().getLogger().domainAddded(domain);
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		try {		
			for (DBConnnection dbc : ddbc.getDBConnnections()) {
				try {
					getManager().removeDB( dbc );
				} catch ( SQLException sqle ) {
					LOGGER.error(sqle.getMessage(), sqle);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}	
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		Domain domain = (Domain) event.getController().getTo();
		getManager().getLogger().domainRemoved(domain);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		updateDomain(domainController.getDomain());
		try {
			domainController.initAccessPolicy();
			domainController.updateParentDomains();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		DomainController domainController = (DomainController) event.getController();
		try {
			domainController.insertOrUpdateAccessPolicy();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}
	
	private void updateDomain( Domain domain ) {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		dac.updateBaseDN(domain.getId());
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
		duc.updateBaseDN(domain.getId());		
		DomainDBConnectionController ddbc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		ddbc.updateBaseDN(domain.getId());		
	}

}