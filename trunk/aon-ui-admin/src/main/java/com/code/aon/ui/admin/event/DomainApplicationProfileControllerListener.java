package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.PROFILE_ACTION_DENIED_CONTROLLER_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.admin.Profile;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.admin.controller.ApplicationProfileController;
import com.code.aon.ui.admin.controller.ProfileActionDeniedController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationProfileControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationProfileControllerListener.class);

	private ProfileActionDeniedController getProfileActionDeniedController() {
		return (ProfileActionDeniedController) AonUtil.getRegisteredBean(PROFILE_ACTION_DENIED_CONTROLLER_NAME);	
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationProfileController apc = (ApplicationProfileController) event.getController();
		try {		
			apc.initProfileInfos();
			getProfileActionDeniedController().init((Profile) apc.getTo());
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
			apc.initProfileInfos();
			getProfileActionDeniedController().init((Profile) apc.getTo());
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

}
