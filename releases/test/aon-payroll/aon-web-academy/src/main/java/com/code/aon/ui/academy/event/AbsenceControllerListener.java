package com.code.aon.ui.academy.event;

import com.code.aon.academy.Absence;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.academy.controller.AbsenceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AbsenceControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AbsenceController absenceController = (AbsenceController)event.getController();
		Absence absence = (Absence)absenceController.getTo();
		try{
			absence.setCourseAlumn(absenceController.getCourseAlumn());
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
		absence.setEvaluation(absenceController.getEvaluation());
	}
	
}
