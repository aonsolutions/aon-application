package com.code.aon.ui.record.event;

import com.code.aon.record.Work;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.WorkController;

public class WorkControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		WorkController courseController = (WorkController)event.getController();
		((Work)courseController.getTo()).setEmployee(courseController.getEmployee());
	}
}
