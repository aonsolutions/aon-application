package com.code.aon.ui.employee.event;

import com.code.aon.record.Position;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.PositionController;

/**
 * The listener class for receiving position events. 
 * 
 * @author iayerbe
 *
 */
public class PositionControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = -4918467524978277977L;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		PositionController positionController = (PositionController)event.getController();
		((Position)positionController.getTo()).setEmployee(positionController.getEmployee());
	}
}
