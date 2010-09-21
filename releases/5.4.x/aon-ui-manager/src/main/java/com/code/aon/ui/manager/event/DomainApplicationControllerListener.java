package com.code.aon.ui.manager.event;

import javax.naming.Name;

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

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		dac.createOrganizationalUnits(dac.getDomainApplication());
		updateApplication(dac);
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		dac.createOrganizationalUnits(dac.getDomainApplication());
		updateApplication(dac);
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
	
	private void updateDBConnection( DomainApplicationController dac ) {
		dac.setAonDB(false);
		DBConnnection dbc = dac.getDomainApplication().getDataSource();
		if ( (dbc != null) && (dbc.getId() != null) ) {
			ManagerController manager = (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
			if ( manager.getDBManager().exists(dbc) ) {
				manager.changeDbConnection(dbc);
				if ( manager.getDBManager().existsTable(dbc, "user") ) {
					dac.setAonDB(true);
					DBBasicController uwg = (DBBasicController) AonUtil.getRegisteredBean(WORK_GROUP_CONTROLLER_NAME);
					uwg.onSearch(null);			
					DBBasicController scopes = (DBBasicController) AonUtil.getRegisteredBean(SCOPE_CONTROLLER_NAME);
					scopes.onSearch(null);			
				}
			}
		}		
	}

}
