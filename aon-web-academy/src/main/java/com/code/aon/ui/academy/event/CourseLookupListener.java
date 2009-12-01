package com.code.aon.ui.academy.event;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseLookupListener extends ControllerAdapter {

	@Override
	public void beforeBeanReset(ControllerEvent event) throws ControllerListenerException {
		try {
			Criteria criteria = event.getController().getCriteria();
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.COURSE_CODE));
			criteria.addOrder(event.getController().getManagerBean().getFieldName(IAcademyAlias.COURSE_START_DATE),false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			IController courseAlumnController = AonUtil.getController("alumnCourse");
			CourseAlumn courseAlumn = (CourseAlumn) courseAlumnController.getTo();
			Course course = (Course) event.getController().getTo();
			courseAlumn.setCourse(course);
		}catch (Exception e) {
		}
	}
	
}
