package com.code.aon.ui.academy.print;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.AonVersion;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseDetailListPrinter implements ICollectionProvider {
	
	private static final Logger LOGGER = Logger.getLogger(CourseDetailListPrinter.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";

	@SuppressWarnings("rawtypes")
	public Collection getCollection() {
		List<ReportCourseAlumn> reportCourseAlumnList = new LinkedList<ReportCourseAlumn>();
		try {
			List courseAlumnList = obtainCourseAlumnList();
			Iterator iter = courseAlumnList.iterator();
			while(iter.hasNext()){
				CourseAlumn courseAlumn = (CourseAlumn)iter.next();
				ReportCourseAlumn reportCourseAlumn = new ReportCourseAlumn();
				reportCourseAlumn.setCourseAlumn(courseAlumn);
				reportCourseAlumn.setPreviousCourse(obtainPreviousCourse(courseAlumn));
				reportCourseAlumn.setBirthDate(obtainBirthDate(courseAlumn.getCustomer().getRegistry()));
				reportCourseAlumn.setPhone(obtainPhone(courseAlumn.getCustomer().getRegistry()));
				reportCourseAlumn.setCellular(obtainCellular(courseAlumn.getCustomer().getRegistry()));
				reportCourseAlumnList.add(reportCourseAlumn);
			}
			return reportCourseAlumnList;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining courseDetailList Collection", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining courseDetailList Collection", e);
		}
		return reportCourseAlumnList;
	}

	@SuppressWarnings("rawtypes")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	private String obtainPreviousCourse(CourseAlumn courseAlumn) {
		try {
			IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID), courseAlumn.getCustomer().getId());
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), courseAlumn.getCourse().getId()));
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ACADEMIC_YEAR_ID), courseAlumn.getCourse().getAcademicYear().getId()));
			criteria.addExpression(ExpressionUtilities.getLessThanExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_START_DATE), courseAlumn.getCourse().getStartDate()));
			criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_START_DATE),false);
			Iterator<ITransferObject> iter = courseAlumnBean.getList(criteria).iterator();
			while(iter.hasNext()){
				CourseAlumn previousCourseAlumn = (CourseAlumn)iter.next();
				return previousCourseAlumn.getCourse().getAcademicYear().getDescription() + " " + previousCourseAlumn.getCourse().getCode();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining previousCourse of alumn with id= " + courseAlumn.getCustomer().getId(), e);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	private List obtainCourseAlumnList() throws ManagerBeanException, ExpressionException {
		CourseController courseController = (CourseController)FormUtil.getController(COURSE_CONTROLLER_NAME);
		IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class.getName());
		Criteria criteria = new Criteria();
		Iterator iter = ((List)courseController.getManagerBean().getList(courseController.getCriteria())).iterator();
		while(iter.hasNext()){
			Course course = (Course)iter.next();
			criteria.addOrExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId().toString());
		}
		criteria.addEqualExpression(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
		criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_CODE));
		criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ACADEMIC_YEAR_ID), false);
		criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID));
		criteria.addOrder(courseAlumnBean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
		return courseAlumnBean.getList(criteria);
	}

	private Date obtainBirthDate(Registry registry) {
		try {
			IManagerBean personBean = BeanManager.getManagerBean(Person.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(personBean.getFieldName(IEntityAlias.PERSON_REGISTRY_ID), registry.getId());
			Iterator<ITransferObject> iter = personBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((Person)iter.next()).getBirthDate();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining birthDate of alumn with id= " + registry.getId(), e);
		}
		return null;
	}

	private String obtainPhone(Registry registry) {
		try {
			IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.FIXED_PHONE);
			Iterator<ITransferObject> iter = registryMediaBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((RegistryMedia)iter.next()).getValue();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining phone of alumn with id= " + registry.getId(), e);
		}
		return null;
	}
	
	private String obtainCellular(Registry registry) {
		try {
			IManagerBean registryMediaBean = BeanManager.getManagerBean(RegistryMedia.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_REGISTRY_ID), registry.getId());
			criteria.addEqualExpression(registryMediaBean.getFieldName(IEntityAlias.REGISTRY_MEDIA_MEDIA_TYPE), MediaType.CELLULAR);
			Iterator<ITransferObject> iter = registryMediaBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((RegistryMedia)iter.next()).getValue();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining cellular of alumn with id= " + registry.getId(), e);
		}
		return null;
	}
	
	public static class ReportCourseAlumn implements ITransferObject {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		private CourseAlumn courseAlumn;
		private String previousCourse;
		private String phone;
		private String cellular;
		private Date birthDate;

		public CourseAlumn getCourseAlumn() {
			return courseAlumn;
		}
		public void setCourseAlumn(CourseAlumn courseAlumn) {
			this.courseAlumn = courseAlumn;
		}
		public String getPreviousCourse() {
			return previousCourse;
		}
		public void setPreviousCourse(String previousCourse) {
			this.previousCourse = previousCourse;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getCellular() {
			return cellular;
		}
		public void setCellular(String cellular) {
			this.cellular = cellular;
		}
		public Date getBirthDate() {
			return birthDate;
		}
		public void setBirthDate(Date birthDate) {
			this.birthDate = birthDate;
		}
	}
}