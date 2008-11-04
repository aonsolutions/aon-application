package com.code.aon.ui.academy.event;

import com.code.aon.academy.Course;
import com.code.aon.ui.academy.controller.AbsenceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAbsenceControllerListener extends ControllerAdapter {
	
	private static final String ABSENCE_CONTROLLER_NAME = "absence_coursealumn";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Course course = (Course)event.getController().getTo();
		AbsenceController absController = (AbsenceController)AonUtil.getController(ABSENCE_CONTROLLER_NAME);
		absController.setCourse(course);
	}
}
