package com.code.aon.ui.manager.event;

import javax.naming.Name;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.DomainApplication;
import com.code.aon.master.IConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DBManagerController;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationControllerListener extends ControllerAdapter implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationControllerListener.class);
	
	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getManager().resetTermsOfServiceAccepted();	
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication application = dac.getDomainApplication();
		setDBConnection(dac, application);
		updateApplication(dac);
		insertApplicationDBDefaults(application);
		getManager().getLogger().domainApplicationAddded(application);
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		updateApplication(dac);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication application = dac.getDomainApplication();
		getManager().getLogger().domainApplicationRemoved(application);
	}	
	
	private void updateApplication( DomainApplicationController dac ) {
		DomainApplication application = dac.getDomainApplication();
		Name applicationId = NameResolver.getApplicationDN(application.getCommonName());
		ManagerController.updateController(ROLE_CONTROLLER_NAME, applicationId);
		ManagerController.updateController(PROFILE_CONTROLLER_NAME, applicationId);
		ManagerController.updateController(DOMAIN_APPLICATION_USER_CONTROLLER_NAME, application.getId());
		ManagerController.updateController(DOMAIN_PROFILE_CONTROLLER_NAME, application.getId());
	}
	
	private void setDBConnection( DomainApplicationController dac, DomainApplication application ) throws ControllerListenerException {
		if ( ! dac.isWithoutDB() ) {
			DBConnnection dbc = application.getDataSource();
			if ( (dbc == null) || (dbc.getId() == null) ) {
				DomainDBConnectionController ddbcc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
				try {
					application.setDataSource( ddbcc.getMasterConnection() );
					dac.getManagerBean().update(application);
				} catch (Throwable e) {
					LOGGER.error(e.getMessage(), e);
					throw new ControllerListenerException( e.getMessage(), e );
				}						
			}
		}		
	}
	
	private void insertApplicationDBDefaults(DomainApplication application) throws ControllerListenerException {
		DBConnnection dbc = application.getDataSource();
		if ( (dbc != null) && (dbc.getId() != null) ) {
			String name = application.getCommonName();
			if ( ArrayUtils.contains(IConstants.DEFAULTS, name) ) {
				DBManagerController dbManager = (DBManagerController) AonUtil.getRegisteredBean(DB_MANAGER_CONTROLLER_NAME);
				try {
					dbManager.insertDefaults(dbc, name);
				} catch (Throwable e) {
					LOGGER.error(e.getMessage(), e);
					throw new ControllerListenerException( e.getMessage(), e );
				}			
			}
		}
	}

}
