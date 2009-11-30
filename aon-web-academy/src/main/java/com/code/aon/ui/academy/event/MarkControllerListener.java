package com.code.aon.ui.academy.event;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.Mark;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseAlumnMarkController;
import com.code.aon.ui.academy.controller.MarkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class MarkControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.MARK_ALUMN_COURSE_CODE));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.MARK_ALUMN_CUSTOMER_REGISTRY_SURNAME));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.MARK_ALUMN_CUSTOMER_REGISTRY_NAME));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.MARK_EVALUATION));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.MARK_SUBJECT_ACADEMIC_SKILL_ID));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAlumnMarkController courseAlumnMarkController = (CourseAlumnMarkController)AonUtil.getController("courseAlumnMark");
		MarkController markController = (MarkController)event.getController();
		Mark mark = (Mark)markController.getTo();
		mark.setAlumn((CourseAlumn)courseAlumnMarkController.getTo());
		mark.setEvaluation(courseAlumnMarkController.getEvaluation());
	}
	
}
