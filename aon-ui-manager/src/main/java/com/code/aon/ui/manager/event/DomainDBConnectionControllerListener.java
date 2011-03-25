package com.code.aon.ui.manager.event;

import com.code.aon.manager.DBConnnection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.manager.controller.DBManagerController;
import com.code.aon.ui.manager.controller.DomainController;
import com.code.aon.ui.manager.controller.DomainDBConnectionController;
import com.code.aon.ui.manager.controller.IManagerConstants;
import com.code.aon.ui.util.AonUtil;

public class DomainDBConnectionControllerListener extends ControllerAdapter implements IManagerConstants {

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainDBConnectionController ddbc = (DomainDBConnectionController) event.getController();
		ddbc.setAonDB( ddbc.updateAonDBConnection(ddbc.getDBConnnection()) );
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
		DomainDBConnectionController ddbc = (DomainDBConnectionController) event.getController();
		DBConnnection dbc = (DBConnnection) event.getController().getTo();
		if ( ddbc.isCreateDB() ) {
			DBManagerController dbManager = (DBManagerController) AonUtil.getRegisteredBean(DB_MANAGER_CONTROLLER_NAME);
			dbManager.createDB(dbc);
		}
		ddbc.setAonDB( ddbc.updateAonDBConnection(ddbc.getDBConnnection()) );
	}
	
}
