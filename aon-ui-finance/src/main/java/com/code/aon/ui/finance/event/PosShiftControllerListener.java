package com.code.aon.ui.finance.event;

import com.code.aon.finance.PosShift;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.finance.controller.PosShiftController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class PosShiftControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PosShiftController controller = (PosShiftController)event.getController();
		controller.setWorkPlace(null);
		controller.setDepartment(null);
		controller.setTotalShiftCountModel(null);
		controller.setFinanceModel(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((PosShift)event.getController().getTo()).setUsername(UserUtils.getInstance().getLoggedUser().getLogin());
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		PosShiftController controller = (PosShiftController)event.getController();
		controller.setTotalShiftCountModel(null);
		controller.setFinanceModel(null);
	}

}
