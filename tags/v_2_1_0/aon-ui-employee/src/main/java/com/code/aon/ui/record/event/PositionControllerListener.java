package com.code.aon.ui.record.event;

import com.code.aon.record.Position;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.PositionController;

public class PositionControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PositionController positionController = (PositionController)event.getController();
		((Position)positionController.getTo()).setEmployee(positionController.getEmployee());
	}
}
