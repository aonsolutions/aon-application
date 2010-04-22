package com.code.aon.ui.employee.event;

import com.code.aon.record.Work;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.WorkController;

/**
 * The employee listener class for receiving work events. 
 * 
 * @author iayerbe
 *
 */
public class EmployeeWorkListener extends ControllerAdapter {
	
	private static final long serialVersionUID = -2124957941464045988L;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		WorkController courseController = (WorkController)event.getController();
		((Work)courseController.getTo()).setEmployee(courseController.getEmployee());
	}
}
