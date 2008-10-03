package com.code.aon.ui.cvitae.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.ui.cvitae.controller.CurriculumController;
import com.code.aon.ui.cvitae.controller.EvaluateController;
import com.code.aon.ui.cvitae.controller.KnowledgeController;
import com.code.aon.ui.cvitae.controller.LanguageController;
import com.code.aon.ui.cvitae.controller.StudiesController;
import com.code.aon.ui.cvitae.controller.WorkExperienceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CurriculumControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(CurriculumControllerListener.class.getName());

	private static final String STUDIES_CONTROLLER_NAME = "Studies";
	private static final String KNOWLEDGE_CONTROLLER_NAME = "Knowledge";
	private static final String WORK_EXPERIENCE_CONTROLLER_NAME = "WorkExperience";
	private static final String LANGUAGE_CONTROLLER_NAME = "language";
	private static final String EVALUATE_CONTROLLER_NAME = "evaluate";

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		((Curriculum)event.getController().getTo()).setDriverLicenses(((CurriculumController)event.getController()).getLicensesAsString());
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		((Curriculum)event.getController().getTo()).setDriverLicenses(((CurriculumController)event.getController()).getLicensesAsString());
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		StudiesController sc = (StudiesController)AonUtil.getController(STUDIES_CONTROLLER_NAME);
		KnowledgeController kc = (KnowledgeController)AonUtil.getController(KNOWLEDGE_CONTROLLER_NAME);
		WorkExperienceController wc = (WorkExperienceController)AonUtil.getController(WORK_EXPERIENCE_CONTROLLER_NAME);
		LanguageController lc = (LanguageController)AonUtil.getController(LANGUAGE_CONTROLLER_NAME);
		EvaluateController ec = (EvaluateController)AonUtil.getController(EVALUATE_CONTROLLER_NAME);
		try {
			sc.onStudies(null);
			kc.onKnowledges(null);
			wc.onWorkExperience(null);
			lc.onLanguages(null);
			ec.onEvaluation(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error initializing curriculum aditional data", e);
		}
	}
}
