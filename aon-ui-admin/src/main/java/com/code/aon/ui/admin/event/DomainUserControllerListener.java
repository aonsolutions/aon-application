package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.GENERAL_SCOPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.config.User;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.DomainController;
import com.code.aon.ui.admin.controller.DomainUserController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainUserControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainUserControllerListener.class);

	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		updateWebmail( duc.getDomainUser() );
		duc.getIdCheck().setOldValue( duc.getDomainUser().getLogin() );
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		duc.getIdCheck().setOldValue( null );
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		duc.resetPassword( duc.getDomainUser() );
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		User user = duc.getDomainUser();
		try {		
			duc.registerScope(user, GENERAL_SCOPE);
			duc.registerWorkGroup(user, GENERAL_SCOPE);					
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		getAdmin().getLogger().domainUserAddded(user);
		updateWebmail(user);
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainUserController duc = (DomainUserController) event.getController();
		User user = duc.getDomainUser();
		getAdmin().getLogger().domainUserdRemoved(user);
	}

	private void updateWebmail( User user ) throws ControllerListenerException {
		try {
			DomainController dc = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
			dc.initWebmail(user);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}	

}
