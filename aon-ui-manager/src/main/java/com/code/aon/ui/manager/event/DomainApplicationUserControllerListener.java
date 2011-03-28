package com.code.aon.ui.manager.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.manager.DomainApplicationUser;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainUserController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationUserControllerListener extends ControllerAdapter implements IManagerConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationUserControllerListener.class);

	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUser user = (DomainApplicationUser) event.getController().getTo();
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(DOMAIN_USER_CONTROLLER_NAME);
		try {		
			duc.registerInDBs(user.getCommonName(), GENERAL_SCOPE);		
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		getManager().getLogger().domainApplicationUserAddded(user);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUser user = (DomainApplicationUser) event.getController().getTo();		
		getManager().getLogger().domainApplicationUserRemoved(user);
	}

}
