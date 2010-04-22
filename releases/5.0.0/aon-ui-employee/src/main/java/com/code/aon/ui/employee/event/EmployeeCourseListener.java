package com.code.aon.ui.employee.event;

import com.code.aon.record.LHCourse;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.record.controller.CourseController;

/**
 * The employee listener class for receiving course events. 
 * 
 * @author iayerbe
 *
 */
public class EmployeeCourseListener extends ControllerAdapter {
	
	private static final long serialVersionUID = -9163080523710888444L;

	/**
	 * Before adding a contract, the current employee is set to the new instance. 
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseController courseController = (CourseController)event.getController();
		((LHCourse)courseController.getTo()).setEmployee(courseController.getEmployee());
	}
}
