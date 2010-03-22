package com.code.aon.ui.academy.event;

import com.code.aon.academy.Course;
import com.code.aon.ui.academy.controller.CourseMarkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseMarksControllerListener extends ControllerAdapter {

	private static final String COURSE_MARK_CONTROLLER_NAME = "courseMark";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Course course = (Course)event.getController().getTo();
        CourseMarkController controller = (CourseMarkController)AonUtil.getRegisteredBean(COURSE_MARK_CONTROLLER_NAME);
        controller.setCourse(course);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Course course = (Course)event.getController().getTo();
        CourseMarkController controller = (CourseMarkController)AonUtil.getRegisteredBean(COURSE_MARK_CONTROLLER_NAME);
        controller.setCourse(course);
	}
	
}
