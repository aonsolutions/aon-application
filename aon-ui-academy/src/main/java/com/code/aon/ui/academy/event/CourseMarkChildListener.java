package com.code.aon.ui.academy.event;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_MARK_CONTROLLER_NAME;

import com.code.aon.academy.Course;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.academy.controller.CourseMarkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseMarkChildListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			setCourse(event);
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			setCourse(event);			
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	private void setCourse(ControllerEvent event) throws ManagerBeanException {
		Course course = (Course) event.getController().getTo();
		CourseMarkController cmc = (CourseMarkController) AonUtil.getRegisteredBean(COURSE_MARK_CONTROLLER_NAME);
		cmc.setCourse(course);
		cmc.setEvaluation(1);
		cmc.refresh();
	}

}
