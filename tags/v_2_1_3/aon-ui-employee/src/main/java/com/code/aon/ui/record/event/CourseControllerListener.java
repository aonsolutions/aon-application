package com.code.aon.ui.record.event;

import com.code.aon.record.Course;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.CourseController;

public class CourseControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseController courseController = (CourseController)event.getController();
		((Course)courseController.getTo()).setEmployee(courseController.getEmployee());
	}
}
