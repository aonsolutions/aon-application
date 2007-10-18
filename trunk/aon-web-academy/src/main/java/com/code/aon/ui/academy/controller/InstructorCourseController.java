package com.code.aon.ui.academy.controller;

import java.util.Iterator;
import java.util.Locale;

import javax.faces.context.FacesContext;

import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.CourseSchedule;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class InstructorCourseController extends LinesController {

	public String getCourseSchedule() throws ManagerBeanException{
		String schedule = new String();
		CourseInstructor courseInstructor = (CourseInstructor)this.getModel().getRowData();
		IManagerBean courseScheduleBean = BeanManager.getManagerBean(CourseSchedule.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseScheduleBean.getFieldName(IAcademyAlias.COURSE_SCHEDULE_COURSE_ID), courseInstructor.getCourse().getId());
		Iterator iter = courseScheduleBean.getList(criteria).iterator();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		while(iter.hasNext()){
			CourseSchedule courseSchedule = (CourseSchedule)iter.next();
			schedule = schedule.concat(courseSchedule.getDay().getShortName(locale) + " ");
		}
		return schedule;
	}
}
