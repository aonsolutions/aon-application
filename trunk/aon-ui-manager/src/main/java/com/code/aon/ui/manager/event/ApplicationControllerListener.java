package com.code.aon.ui.manager.event;

import com.code.aon.manager.Application;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.ApplicationController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;

public class ApplicationControllerListener extends ControllerAdapter implements IManagerConstants {

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationController controller = (ApplicationController) event.getController();
		updateApplication(controller.getApplication());
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ApplicationController controller = (ApplicationController) event.getController();
		updateApplication(controller.getApplication());
	}
	
	private void updateApplication( Application application ) {
		ManagerController.updateController(ROLE_CONTROLLER_NAME, application.getId());
		ManagerController.updateController(PROFILE_CONTROLLER_NAME, application.getId());
	}

}
