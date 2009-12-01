package com.code.aon.ui.academy.event;

import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class InstructorCourseControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addEqualExpression(event.getController().getManagerBean().getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_COURSE_STATUS), CourseStatus.ACTIVE);
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_COURSE_CODE));
			event.getController().setCriteria(criteria);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}