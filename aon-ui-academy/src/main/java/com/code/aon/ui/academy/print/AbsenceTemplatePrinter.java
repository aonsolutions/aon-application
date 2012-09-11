package com.code.aon.ui.academy.print;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AbsenceTemplatePrinter implements ICollectionProvider {

	protected static final String COURSE_CONTROLLER_NAME = "course";

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<AbsenceReportTo> list = new ArrayList<AbsenceReportTo>();
		CourseController courseController = (CourseController) FormUtil.getController(COURSE_CONTROLLER_NAME);
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
	protected List obtainCourseAlumnList(Course course)throws ManagerBeanException {
		IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
		criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
		criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
		return courseAlumnBean.getList(criteria);
	}

	@SuppressWarnings("unchecked")
	protected TaskHolder obtainCourseInstructor(Course course)throws ManagerBeanException {
		IManagerBean courseInstructorBean = BeanManager.getManagerBean(CourseInstructor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseInstructorBean.getFieldName(IEntityAlias.COURSE_INSTRUCTOR_COURSE_ID),course.getId());
		Iterator iter = courseInstructorBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			int id = ((CourseInstructor) iter.next()).getEmployee();
			IManagerBean thBean = BeanManager.getManagerBean(CourseInstructor.class);
			return (TaskHolder) thBean.get(id);
		}
		return null;
	}
	
	public class AbsenceReportTo implements ITransferObject{
		
		private Course course;
		private List<CourseAlumn> courseAlumns;
		private TaskHolder instructor;
		
		public Course getCourse() {
			return course;
		}
		public void setCourse(Course course) {
			this.course = course;
		}
		public List<CourseAlumn> getCourseAlumns() {
			return courseAlumns;
		}
		public void setCourseAlumns(List<CourseAlumn> courseAlumns) {
			this.courseAlumns = courseAlumns;
		}
		public TaskHolder getInstructor() {
			return instructor;
		}
		public void setInstructor(TaskHolder instructor) {
			this.instructor = instructor;
		}
	}
}