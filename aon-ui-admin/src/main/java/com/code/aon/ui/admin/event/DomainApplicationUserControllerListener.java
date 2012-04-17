package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_USER_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.GENERAL_SCOPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.ApplicationUser;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.DomainUserController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationUserControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationUserControllerListener.class);

	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationUser user = (ApplicationUser) event.getController().getTo();
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
		try {		
			duc.registerScope(user.getUser(), GENERAL_SCOPE);
			duc.registerWorkGroup(user.getUser(), GENERAL_SCOPE);		
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		getAdmin().getLogger().domainApplicationUserAddded(user);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationUser user = (ApplicationUser) event.getController().getTo();		
		getAdmin().getLogger().domainApplicationUserRemoved(user);
	}

}
