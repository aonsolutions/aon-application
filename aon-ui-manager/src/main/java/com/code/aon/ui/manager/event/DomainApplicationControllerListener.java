package com.code.aon.ui.manager.event;

import javax.naming.Name;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.DomainApplication;
import com.code.aon.master.IConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainApplicationUserController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.manager.controller.ProfileController;
import com.code.aon.ui.manager.controller.RoleController;
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
		updateDataSources();
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication application = dac.getDomainApplication();
		updateApplication(dac);
		insertApplicationDBDefaults(application);
		getManager().getLogger().domainApplicationAddded(application);
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		updateApplication(dac);
		updateDataSources();
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
		DomainApplicationUserController dau = (DomainApplicationUserController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_USER_CONTROLLER_NAME);
		dau.updateBaseDN(application.getId());
		dau.onSearch(null);		
		RoleController rc = (RoleController) AonUtil.getRegisteredBean(ROLE_CONTROLLER_NAME);
		Name applicationId = NameResolver.getApplicationDN(application.getCommonName()); 
		rc.updateBaseDN(applicationId);
		rc.onSearch(null);
		ProfileController pc = (ProfileController) AonUtil.getRegisteredBean(DOMAIN_PROFILE_CONTROLLER_NAME);
		pc.updateBaseDN(application.getId());
		pc.onSearch(null);
		updateDBConnection(dac);
	}
	
	private void updateDataSources() {
		DomainDBConnectionController ddbcc = (DomainDBConnectionController) AonUtil.getRegisteredBean(DOMAIN_DB_CONNECTION_CONTROLLER_NAME);
		ddbcc.updateDataSources();		
	}
	
	private void updateDBConnection( DomainApplicationController dac ) {
		dac.setAonDB(false);
		DBConnnection dbc = dac.getDomainApplication().getDataSource();
		if ( (dbc != null) && (dbc.getId() != null) ) {
			ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			if ( manager.getDBManager().exists(dbc) ) {
				manager.changeDbConnection(dbc);
				if ( manager.getDBManager().existsTable(dbc, "user") ) {
					dac.setAonDB(true);
					IController uwg = FormUtil.getController(WORK_GROUP_CONTROLLER_NAME);
					uwg.onSearch(null);			
					IController scopes = FormUtil.getController(SCOPE_CONTROLLER_NAME);
					scopes.onSearch(null);			
				}
			}
		}		
	}
	
	private void insertApplicationDBDefaults(DomainApplication application) throws ControllerListenerException {
		DBConnnection dbc = application.getDataSource();
		if ( (dbc != null) && (dbc.getId() != null) ) {
			String name = application.getCommonName();
			if ( ArrayUtils.contains(IConstants.DEFAULTS, name) ) {
				ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
				try {
					manager.insertDefaults(dbc, name);
				} catch (Throwable e) {
					LOGGER.error(e.getMessage(), e);
					throw new ControllerListenerException( e.getMessage(), e );
				}			
			}
		}
	}

}
