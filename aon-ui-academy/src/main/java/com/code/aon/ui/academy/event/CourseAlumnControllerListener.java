package com.code.aon.ui.academy.event;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_ALUMN_OBSERVATION_CONTROLLER_NAME;
import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_MARK_CONTROLLER_NAME;
import static com.code.aon.ui.academy.controller.IAcademyConstants.CUSTOMER_ABSENCE_CONTROLLER_NAME;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.academy.controller.CourseMarkController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		resetModels();
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		resetModels();
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		resetModels();
	}

	private void resetModels() throws ControllerListenerException {
		FormUtil.getController(COURSE_ALUMN_OBSERVATION_CONTROLLER_NAME).initializeModel();
		FormUtil.getController(CUSTOMER_ABSENCE_CONTROLLER_NAME).initializeModel();
		try {
			CourseMarkController cmc = (CourseMarkController) AonUtil.getRegisteredBean(COURSE_MARK_CONTROLLER_NAME);
			cmc.refresh();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
}
