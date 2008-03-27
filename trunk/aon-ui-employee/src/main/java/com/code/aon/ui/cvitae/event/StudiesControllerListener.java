package com.code.aon.ui.cvitae.event;

import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.Studies;
import com.code.aon.ui.cvitae.controller.CurriculumController;
import com.code.aon.ui.cvitae.controller.StudiesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class StudiesControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CurriculumController curriculumController = 
			(CurriculumController) AonUtil.getController( CurriculumController.MANAGER_BEAN_NAME );
		((Studies)event.getController().getTo()).setCurriculum((Curriculum)curriculumController.getTo());
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		StudiesController cvStudiesController = (StudiesController)event.getController();
		cvStudiesController.onReset(null);
	}
}
