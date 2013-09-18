package com.code.aon.ui.academy.event;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_ALUMN_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonMessages.ACADEMY_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.COURSE_ALUMN_LIMIT;

import javax.faces.event.AbortProcessingException;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.ui.academy.controller.CourseAlumnController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CustomerCourseControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAlumn courseAlumn = (CourseAlumn)event.getController().getTo();
		CourseAlumnController cac = (CourseAlumnController) AonUtil.getRegisteredBean(COURSE_ALUMN_CONTROLLER_NAME);
		if ( cac.isLimitReached(courseAlumn.getCourse()) ) {
			String message = AonUtil.getMessage(ACADEMY_BUNDLE, COURSE_ALUMN_LIMIT);
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}

}