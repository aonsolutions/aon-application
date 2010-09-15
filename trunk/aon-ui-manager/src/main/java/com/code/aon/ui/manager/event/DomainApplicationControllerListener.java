package com.code.aon.ui.manager.event;

import java.sql.SQLException;

import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.DBConnnection;
import com.code.aon.manager.DomainApplication;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DBBasicController;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainApplicationUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.manager.controller.ProfileController;
import com.code.aon.ui.manager.controller.RoleController;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationControllerListener.class);
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		dac.createOrganizationalUnits(dac.getDomainApplication());
		try {
			updateApplication(dac);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		dac.createOrganizationalUnits(dac.getDomainApplication());
		try {
			updateApplication(dac);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	private void updateApplication( DomainApplicationController dac ) throws SQLException {
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
	
	private void updateDBConnection( DomainApplicationController dac ) throws SQLException {
		dac.setAonDB(false);
		DBConnnection dbc = dac.getDomainApplication().getDataSource();
		if ( (dbc != null) && (dbc.getId() != null) ) {
			ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			manager.changeDbConnection(dbc);
			if ( manager.getDBManager().existsTable(dbc, "user") ) {
				dac.setAonDB(true);
				DBBasicController uwg = (DBBasicController) AonUtil.getRegisteredBean(WORK_GROUP_CONTROLLER_NAME);
				uwg.updateDAO();
				uwg.onSearch(null);			
				DBBasicController scopes = (DBBasicController) AonUtil.getRegisteredBean(SCOPE_CONTROLLER_NAME);
				scopes.updateDAO();
				scopes.onSearch(null);			
			}
		}		
	}

}
