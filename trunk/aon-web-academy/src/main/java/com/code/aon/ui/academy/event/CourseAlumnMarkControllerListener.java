package com.code.aon.ui.academy.event;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseAlumnMarkController;
import com.code.aon.ui.academy.controller.MarkController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnMarkControllerListener extends ControllerAdapter {

	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addEqualExpression(event.getController().getManagerBean().getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
		} catch (ManagerBeanException e) {
		}
		
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			MarkController markController = (MarkController)AonUtil.getController("mark");
			markController.updateCriteria((CourseAlumn)event.getController().getTo(), ((CourseAlumnMarkController)event.getController()).getEvaluation());
			markController.onSearch(null);
		}catch (ManagerBeanException e) {
		}
	}
}
