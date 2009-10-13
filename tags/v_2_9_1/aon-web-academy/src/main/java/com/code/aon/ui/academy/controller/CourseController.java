package com.code.aon.ui.academy.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.print.ReportCourse;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class CourseController extends BasicController implements ICollectionProvider {
	
	private static final Logger LOGGER = Logger.getLogger(CourseController.class.getName());
	
	public void onEditSearch(MenuEvent event){
		this.onEditSearch((ActionEvent)event);
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(){
		List<ReportCourse> reportCourseList = new LinkedList<ReportCourse>();
		try {
			Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
			while(iter.hasNext()){
				Course course = (Course)iter.next();
				ReportCourse reportCourse = new ReportCourse();
				reportCourse.setCourse(course);
				reportCourse.setAlumnCount(obtainAlumnCount(course));
				reportCourseList.add(reportCourse);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining course collection", e);
		}
		return reportCourseList;
	}

	private int obtainAlumnCount(Course course) {
		try {
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			return courseAlumnBean.getCount(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining alumnCount for course with id= " + course.getId(), e);
		}
		return 0;
	}
}