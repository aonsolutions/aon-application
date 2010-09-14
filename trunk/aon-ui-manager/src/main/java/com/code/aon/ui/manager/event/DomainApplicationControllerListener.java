package com.code.aon.ui.manager.event;

import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.Domain;
import com.code.aon.manager.DomainApplication;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainApplicationController;
import com.code.aon.ui.manager.controller.DomainApplicationUserController;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ProfileController;
import com.code.aon.ui.manager.controller.RoleController;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationControllerListener.class);
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController controller = (DomainApplicationController) event.getController();
		DomainApplication domainApplication = controller.getDomainApplication();
		controller.createOrganizationalUnits(domainApplication);
	}	
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication domainApplication = dac.getDomainApplication();
		dac.createOrganizationalUnits(domainApplication);
		updateApplication(domainApplication);
	}
	
	private void updateApplication( DomainApplication application ) {
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
	}

}
