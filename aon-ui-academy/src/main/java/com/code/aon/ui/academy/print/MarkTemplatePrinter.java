package com.code.aon.ui.academy.print;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.AonVersion;
import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.CourseInstructor;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MarkTemplatePrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(MarkTemplatePrinter.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		List<ReportTemplateMark> reportTemplateMarkList = new LinkedList<ReportTemplateMark>();
		try{
			CourseController courseController = (CourseController)FormUtil.getController(COURSE_CONTROLLER_NAME);
			Iterator iter = ((List)courseController.getModel().getWrappedData()).iterator();
			while (iter.hasNext()){
				Course course = (Course)iter.next();
				ReportTemplateMark reportTemplateMark = new ReportTemplateMark();
				reportTemplateMark.setCourse(course);
				reportTemplateMark.setInstructor(obtainCourseInstructor(course));
				obtainDetails(reportTemplateMark,course);
				reportTemplateMarkList.add(reportTemplateMark);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Collection", e);
		}
		return reportTemplateMarkList;
	}

	private TaskHolder obtainCourseInstructor(Course course) throws ManagerBeanException {
		IManagerBean courseInstructorBean = BeanManager.getManagerBean(CourseInstructor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseInstructorBean.getFieldName(IEntityAlias.COURSE_INSTRUCTOR_COURSE_ID), course.getId());
		List<ITransferObject> list = courseInstructorBean.getList(criteria);
		if (! list.isEmpty() ) {
			CourseInstructor ci = (CourseInstructor) list.get(0);
			return ci.getTaskHolder();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	private void obtainDetails(ReportTemplateMark reportTemplateMark, Course course){
		List<CourseAlumn> courseAlumns = obtainAlumns(course);
		Iterator<CourseAlumn> courseAlumnsIter = courseAlumns.iterator(); 
		List<CourseAcademicSkill> courseAcademicSkills = obtainSkills(course);
		while (courseAlumnsIter.hasNext()){
			Iterator<CourseAcademicSkill> courseAcademicSkillIter = courseAcademicSkills.iterator();
			Customer customer = courseAlumnsIter.next().getCustomer();
			while (courseAcademicSkillIter.hasNext()){
				reportTemplateMark.addDetail(courseAcademicSkillIter.next().getAcademicSkill(), customer);
			}
		}
	}
	
	private List<CourseAlumn> obtainAlumns(Course course){
		try {
			IManagerBean bean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			criteria.addOrder(bean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
			List<CourseAlumn> lst = new ArrayList<CourseAlumn>();
			Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
			while (iter.hasNext()){
				lst.add((CourseAlumn)iter.next());
			}
			return lst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining alumn with course = " + course.getId(), e);
		}
		return null;
	}

	private List<CourseAcademicSkill> obtainSkills(Course course){
		try {
			IManagerBean bean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), course.getId());
			List<CourseAcademicSkill> lst = new ArrayList<CourseAcademicSkill>();
			Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
			while (iter.hasNext()){
				lst.add((CourseAcademicSkill)iter.next());
			}
			return lst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining academic skills with course = " + course.getId(), e);
		}
		return null;
	}
	
	public static class ReportTemplateMark implements ITransferObject {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private Course course;
		private TaskHolder instructor;
		private List<TemplateMarkDetail> details = new ArrayList<TemplateMarkDetail>();

		public Course getCourse() {
			return course;
		}
		public void setCourse(Course course) {
			this.course = course;
		}
		public TaskHolder getInstructor() {
			return instructor;
		}
		public void setInstructor(TaskHolder instructor) {
			this.instructor = instructor;
		}
		public List<TemplateMarkDetail> getDetails() {
			return details;
		}

		public TemplateMarkDetail addDetail(AcademicSkill academicSkill,Customer customer){
			TemplateMarkDetail detail = new TemplateMarkDetail();
			detail.setAcademicSkill(academicSkill);
			detail.setAlumn(customer);
			details.add(detail);
			return detail;
		}
		
	}

	private static class TemplateMarkDetail implements ITransferObject {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private AcademicSkill academicSkill;
		private Customer alumn;
		
		public AcademicSkill getAcademicSkill() {
			return academicSkill;
		}
		public void setAcademicSkill(AcademicSkill academicSkill) {
			this.academicSkill = academicSkill;
		}
		public Customer getAlumn() {
			return alumn;
		}
		public void setAlumn(Customer alumn) {
			this.alumn = alumn;
		}
	}

}