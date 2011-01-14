package com.code.aon.ui.manager.event;

import com.code.aon.manager.DBConnnection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.manager.controller.ManagerController;
import com.code.aon.ui.util.AonUtil;

public class DomainDBConnectionControllerListener extends ControllerAdapter implements IManagerConstants {

	private ManagerController getManager() {
		return (ManagerController) AonUtil.getRegisteredBean(MANAGER_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		DomainDBConnectionController controller = (DomainDBConnectionController) event.getController();
		DBConnnection dbc = (DBConnnection) controller.getTo();
		DomainController domainController = (DomainController) AonUtil.getRegisteredBean(DOMAIN_CONTROLLER_NAME);
		controller.init(dbc, domainController.getDomain().getCommonName());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainDBConnectionController controller = (DomainDBConnectionController) event.getController();
		if ( controller.isCreateDB() ) {
			DBConnnection dbc = (DBConnnection) event.getController().getTo();
			getManager().createDB(dbc);
		}
	}

}
