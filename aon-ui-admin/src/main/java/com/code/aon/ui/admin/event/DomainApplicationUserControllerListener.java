package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.GENERAL_SCOPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.config.ApplicationUser;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.DomainApplicationController;
import com.code.aon.ui.admin.controller.DomainApplicationUserController;
import com.code.aon.ui.admin.controller.DomainController;
import com.code.aon.ui.admin.controller.DomainUserController;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationUserControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationUserControllerListener.class);

	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(IAdminConstants.ADMIN_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUserController dauc = (DomainApplicationUserController) event.getController();
		try {		
			dauc.updateUserProfiles();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_APPLICATION_CONTROLLER_NAME);
		Integer domain = dac.getDomainApplication().getDomain();
		if ( dauc.isShowParentDomainUsers() ) {
			DomainController dc = (DomainController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_CONTROLLER_NAME);
			domain = dc.getParentDomain().getId();
			dauc.setShowParentDomainUsers(false);
		}
		dac.updateAvailableUsers(domain);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUserController dauc = (DomainApplicationUserController) event.getController();
		try {		
			dauc.updateUserProfiles();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUserController dauc = (DomainApplicationUserController) event.getController();
		ApplicationUser user = dauc.getApplicationUser();
		DomainUserController duc = (DomainUserController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_USER_CONTROLLER_NAME);
		try {		
			dauc.insertUserProfiles();
			duc.registerScope(user.getUser(), GENERAL_SCOPE);
			duc.registerWorkGroup(user.getUser(), GENERAL_SCOPE);		
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
		getAdmin().getLogger().domainApplicationUserAddded(user);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUserController dauc = (DomainApplicationUserController) event.getController();
		try {		
			dauc.insertUserProfiles();
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationUserController dauc = (DomainApplicationUserController) event.getController();
		try {		
			DomainApplicationUserController.removeUserProfiles( dauc.getApplicationUser() );
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationUser user = (ApplicationUser) event.getController().getTo();		
		getAdmin().getLogger().domainApplicationUserRemoved(user);
	}

}
