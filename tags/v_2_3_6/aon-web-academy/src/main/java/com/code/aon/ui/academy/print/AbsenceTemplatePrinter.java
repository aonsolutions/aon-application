package com.code.aon.ui.academy.print;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.print.AbsenceReportTo;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.util.AonUtil;

public class AbsenceTemplatePrinter implements ICollectionProvider {

	private static final String COURSE_CONTROLLER_NAME = "course";

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<AbsenceReportTo> list = new ArrayList<AbsenceReportTo>();
		CourseController courseController = (CourseController) AonUtil.getController(COURSE_CONTROLLER_NAME);
		try {
			if (courseController.getTo() != null) {
				Course course = (Course) courseController.getTo();
				AbsenceReportTo absenceReportTo = new AbsenceReportTo();
				absenceReportTo.setCourse(course);
				absenceReportTo.setCourseAlumns(obtainCourseAlumnList(course));
				absenceReportTo.setInstructor(obtainCourseInstructor(course));
				list.add(absenceReportTo);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to load collection to print");
			throw new AbortProcessingException();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	@SuppressWarnings("unchecked")
	private List obtainCourseAlumnList(Course course)throws ManagerBeanException {
		IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
		criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
		criteria.addOrder(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_SURNAME));
		criteria.addOrder(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
		return courseAlumnBean.getList(criteria);
	}

	@SuppressWarnings("unchecked")
	private Employee obtainCourseInstructor(Course course)throws ManagerBeanException {
		IManagerBean courseInstructorBean = BeanManager.getManagerBean(CourseInstructor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseInstructorBean.getFieldName(IAcademyAlias.COURSE_INSTRUCTOR_COURSE_ID),course.getId());
		Iterator iter = courseInstructorBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return ((CourseInstructor) iter.next()).getEmployee();
		}
		return null;
	}
}