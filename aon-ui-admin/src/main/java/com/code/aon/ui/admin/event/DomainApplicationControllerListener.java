package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class DomainApplicationControllerListener extends ControllerAdapter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationControllerListener.class);
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getAdmin().resetTermsOfServiceAccepted();	
	}

}
