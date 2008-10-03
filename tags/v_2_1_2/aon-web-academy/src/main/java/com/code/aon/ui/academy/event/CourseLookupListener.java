package com.code.aon.ui.academy.event;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseLookupListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			IController courseAlumnController = AonUtil.getController("alumnCourse");
			CourseAlumn courseAlumn = (CourseAlumn) courseAlumnController.getTo();
			Course course = (Course) event.getController().getTo();
			courseAlumn.setCourse( course );
		}catch (Exception e) {
		}
	}
	
}
