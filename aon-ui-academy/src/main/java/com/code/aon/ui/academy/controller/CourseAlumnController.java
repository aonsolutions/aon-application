package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_CONTROLLER_NAME;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseAlumnController extends LinesController {
	
	public boolean isLimitReached() throws ManagerBeanException{
		IController courseController = FormUtil.getController(COURSE_CONTROLLER_NAME);
		Course course = (Course)courseController.getTo();
		if(course != null){
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			return (courseAlumnBean.getCount(criteria) >= course.getAlumnLimit());
		}
		return false;
	}
}
