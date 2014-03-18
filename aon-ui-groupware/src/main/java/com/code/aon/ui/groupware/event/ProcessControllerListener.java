package com.code.aon.ui.groupware.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;

public class ProcessControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController detailController = FormUtil.getController(IGroupWareConstants.PROCESS_DETAIL_CONTROLLER_NAME);
		detailController.onReset(null);
	}
	
}
