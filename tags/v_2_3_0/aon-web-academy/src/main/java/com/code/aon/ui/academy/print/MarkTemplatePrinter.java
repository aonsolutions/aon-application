package com.code.aon.ui.academy.print;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseAlumnStatus;
import com.code.aon.academy.print.ReportTemplateMark;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.academy.controller.CourseController;
import com.code.aon.ui.util.AonUtil;

public class MarkTemplatePrinter implements ICollectionProvider{

	private static final Logger LOGGER = Logger.getLogger(MarkTemplatePrinter.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List<ReportTemplateMark> reportTemplateMarkList = new LinkedList<ReportTemplateMark>();
		try{
			CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
			Iterator iter = ((List)courseController.getModel().getWrappedData()).iterator();
			while (iter.hasNext()){
				Course course = (Course)iter.next();
				ReportTemplateMark reportTemplateMark = new ReportTemplateMark();
				reportTemplateMark.setCourse(course);
				obtainDetails(reportTemplateMark,course);
				reportTemplateMarkList.add(reportTemplateMark);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Collection", e);
		}
		return reportTemplateMarkList;
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	private List<CourseAlumn> obtainAlumns(Course course){
		try {
			IManagerBean bean = BeanManager.getManagerBean(CourseAlumn.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
			criteria.addEqualExpression(bean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), CourseAlumnStatus.ACTIVE);
			criteria.addOrder(bean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_SURNAME));
			criteria.addOrder(bean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_REGISTRY_NAME));
			List<CourseAlumn> lst = new ArrayList<CourseAlumn>();
			Iterator iter = bean.getList(criteria).iterator();
			while (iter.hasNext()){
				lst.add((CourseAlumn)iter.next());
			}
			return lst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining alumn with course = " + course.getId(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<CourseAcademicSkill> obtainSkills(Course course){
		try {
			IManagerBean bean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), course.getId());
			List<CourseAcademicSkill> lst = new ArrayList<CourseAcademicSkill>();
			Iterator iter = bean.getList(criteria).iterator();
			while (iter.hasNext()){
				lst.add((CourseAcademicSkill)iter.next());
			}
			return lst;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining academic skills with course = " + course.getId(), e);
		}
		return null;
	}
}