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
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addEqualExpression(event.getController().getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			criteria.addOrder(event.getController().getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_SURNAME));
			criteria.addOrder(event.getController().getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
		
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			MarkController markController = (MarkController)AonUtil.getController("mark");
			markController.updateCriteria((CourseAlumn)event.getController().getTo(), ((CourseAlumnMarkController)event.getController()).getEvaluation());
			markController.onSearch(null);
		}catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}
