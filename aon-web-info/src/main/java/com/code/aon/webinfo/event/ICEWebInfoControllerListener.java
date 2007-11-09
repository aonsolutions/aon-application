package com.code.aon.webinfo.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webinfo.controller.ICECompanyWebInfoController;

public class ICEWebInfoControllerListener extends ControllerAdapter {
	
	private static final String WEB_INFO_CONTROLLER_NAME = "companyWebInfo";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			ICECompanyWebInfoController webInfoController = (ICECompanyWebInfoController)AonUtil.getController(WEB_INFO_CONTROLLER_NAME);
			webInfoController.onLoadWebInfo(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Erro loading asociated Web Info", e);
		}
	}
}
