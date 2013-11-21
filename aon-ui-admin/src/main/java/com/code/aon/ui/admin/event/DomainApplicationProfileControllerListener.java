package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.PROFILE_ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.Profile;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.ui.admin.controller.ApplicationProfileController;
import com.code.aon.ui.admin.controller.ProfileActionDeniedController;
import com.code.aon.ui.audit.controller.ActionDeniedController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationProfileControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationProfileControllerListener.class);

	private ProfileActionDeniedController getProfileActionDeniedController() {
		return (ProfileActionDeniedController) AonUtil.getRegisteredBean(PROFILE_ACTION_DENIED_CONTROLLER_NAME);	
	}

	private ActionDeniedController getActionDeniedController() {
		return (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);	
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			Set<Module> enabledModules = getActionDeniedController().getEnabledModules(null, true);
			apc.initProfileInfos( enabledModules );
			getProfileActionDeniedController().init((Profile) apc.getTo(), enabledModules);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			Set<Module> enabledModules = getActionDeniedController().getEnabledModules(null, true);
			apc.initProfileInfos( enabledModules );
			getProfileActionDeniedController().init((Profile) apc.getTo(), enabledModules);
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			apc.onSaveProfile();
			getProfileActionDeniedController().update();
			updateUserProfile( (Profile) apc.getTo() );
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}		
	}	

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			ApplicationProfileController.removeLines( (Profile) apc.getTo() );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}	
	}	
	
	private void updateUserProfile( Profile profile ) {
		Integer applicationUser = AdminUtil.getApplicationUser(AonUtil.getAuthPrincipal());
		if ( applicationUser != null ) {
			List<Integer> profiles = AdminUtil.getProfiles(applicationUser);
			if ( profiles != null && profiles.contains(profile.getId()) ) {
				getActionDeniedController().initCurrentUser();
			}
		}
	}

}
