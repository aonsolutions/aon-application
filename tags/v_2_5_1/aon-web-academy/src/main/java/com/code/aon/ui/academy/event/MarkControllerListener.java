package com.code.aon.ui.academy.event;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.Mark;
import com.code.aon.ui.academy.controller.CourseAlumnMarkController;
import com.code.aon.ui.academy.controller.MarkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class MarkControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAlumnMarkController courseAlumnMarkController = (CourseAlumnMarkController)AonUtil.getController("courseAlumnMark");
		MarkController markController = (MarkController)event.getController();
		Mark mark = (Mark)markController.getTo();
		mark.setAlumn((CourseAlumn)courseAlumnMarkController.getTo());
		mark.setEvaluation(courseAlumnMarkController.getEvaluation());
	}
	
}
