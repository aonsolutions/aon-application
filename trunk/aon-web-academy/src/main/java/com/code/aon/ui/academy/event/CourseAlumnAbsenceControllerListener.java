package com.code.aon.ui.academy.event;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.academy.controller.AbsenceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnAbsenceControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AbsenceController absenceController = (AbsenceController)AonUtil.getController("absence_coursealumn");
		try{
			absenceController.setCustomer(getCustomer(event));
			absenceController.setCourse(getCourse());
			absenceController.onSearchAbsences(null);
		}catch (ManagerBeanException e) {
		}
	}

	private Customer getCustomer(ControllerEvent event) throws ManagerBeanException{
		return ((CourseAlumn)event.getController().getTo()).getCustomer();
	}
	
	private Course getCourse() throws ManagerBeanException{
		return (Course)AonUtil.getController("course").getTo();
	}
}
