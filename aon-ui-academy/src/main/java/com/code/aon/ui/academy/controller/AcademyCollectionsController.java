package com.code.aon.ui.academy.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.academy.AcademicYear;
import com.code.aon.academy.CourseLevel;
import com.code.aon.academy.CourseSubject;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AcademyCollectionsController {
	
	private List<SelectItem> courseStatuses;
	
    public AcademicYear getAcademicYear() {
		return null;
	}

	public void setAcademicYear(AcademicYear academicYear) {
	}
	
    public List<SelectItem> getAcademicYears() throws ManagerBeanException{
        List<SelectItem> academicYears = new LinkedList<SelectItem>();
        IManagerBean academicYearBean = BeanManager.getManagerBean(AcademicYear.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(academicYearBean.getFieldName(IEntityAlias.ACADEMIC_YEAR_DESCRIPTION), false);
        for( ITransferObject to : academicYearBean.getList(criteria) ) {
            AcademicYear academicYear = (AcademicYear) to;
            SelectItem item = new SelectItem(academicYear, academicYear.getDescription());
            academicYears.add(item);
        }
        return academicYears;
    }

    public CourseSubject getCourseSubject() {
		return null;
	}

	public void setCourseSubject(CourseSubject courseSubject) {
	}
    
    public List<SelectItem> getCourseSubjects() throws ManagerBeanException{
        List<SelectItem> courseSubjects = new LinkedList<SelectItem>();
        IManagerBean courseSubjectBean = BeanManager.getManagerBean(CourseSubject.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(courseSubjectBean.getFieldName(IEntityAlias.COURSE_SUBJECT_DESCRIPTION));
        for( ITransferObject to : courseSubjectBean.getList(criteria) ) {
            CourseSubject subject = (CourseSubject) to;
            SelectItem item = new SelectItem(subject, subject.getDescription());
            courseSubjects.add(item);
        }
        return courseSubjects;
    }
    
    public CourseLevel getCourseLevel() {
		return null;
	}

	public void setCourseLevel(CourseLevel courseLevel) {
	}

    public List<SelectItem> getCourseLevels() throws ManagerBeanException{
		List<SelectItem> courseLevels = new LinkedList<SelectItem>();
		IManagerBean courseLevelBean = BeanManager.getManagerBean(CourseLevel.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(courseLevelBean.getFieldName(IEntityAlias.COURSE_LEVEL_DESCRIPTION));
		for( ITransferObject to : courseLevelBean.getList(criteria) ) {
			CourseLevel level = (CourseLevel) to;
			SelectItem item = new SelectItem(level, level.getDescription());
			courseLevels.add(item);
		}
		return courseLevels;
	}

    
	public List<SelectItem> getCourseStatuses() {
		if (courseStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			courseStatuses = new LinkedList<SelectItem>();
			for( CourseStatus status : CourseStatus.values() ) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				courseStatuses.add(item);			
			}
		}
		return courseStatuses;
	}
	
	/*
	
	public List<SelectItem> getInstructors() throws ManagerBeanException {
		List<SelectItem> employees = new LinkedList<SelectItem>();
		IManagerBean employeeBean = BeanManager.getManagerBean(Employee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(employeeBean.getFieldName(ICompanyAlias.EMPLOYEE_ACTIVE), new Boolean(true));
		criteria.addOrder(employeeBean.getFieldName(ICompanyAlias.EMPLOYEE_REGISTRY_NAME));
		Iterator<ITransferObject> iter = employeeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Employee employee = (Employee) iter.next();
			SelectItem item = new SelectItem(employee.getId(), employee.getRegistry().getName());
			employees.add(item);
		}
		return employees;
	}
	
	public List<SelectItem> getInstructorTypes() {
		List<SelectItem> instructorTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		InstructorType[] types = InstructorType.values();
		for (int i = 0; i < types.length; i++) {
			InstructorType type = types[i];
			String name = type.getName(locale);
			SelectItem item = new SelectItem(type, name);
			instructorTypes.add(item);
		}
		return instructorTypes;
	}
	
	public List<SelectItem> getWeekDays(){
		List<SelectItem> weekDays = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		WeekDay[] days = WeekDay.values();
		for (int i = 0; i < days.length; i++) {
			WeekDay day = days[i];
			String name = day.getName(locale);
			SelectItem item = new SelectItem(day, name);
			weekDays.add(item);
		}
		return weekDays;
	}
	
	@SuppressWarnings("unchecked")
    public List<SelectItem> getCourseAlumns() throws ManagerBeanException{
        List<SelectItem> courseAlumns = new LinkedList<SelectItem>();
        try{
            CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
            Customer customer = (Customer)customerController.getTo();

            IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_CUSTOMER_ID), customer.getId());
            criteria.addExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_STATUS), ""+CourseAlumnStatus.ACTIVE.ordinal());
            Iterator iter= courseAlumnBean.getList(criteria).iterator();
            while(iter.hasNext()){
            	CourseAlumn courseAlumn = (CourseAlumn)iter.next();
                SelectItem item = new SelectItem(courseAlumn.getId(), courseAlumn.getCourse().getDescription());
                courseAlumns.add(item);
            }
            return courseAlumns;
        }catch (Exception e) {
        	throw new ManagerBeanException(e);
		}
    }

	@SuppressWarnings("unchecked")
    public List<SelectItem> getAcademicSkills() throws ManagerBeanException{
        List<SelectItem> academicSkills = new LinkedList<SelectItem>();
        IManagerBean academicSkillBean = BeanManager.getManagerBean(AcademicSkill.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(academicSkillBean.getFieldName(IAcademyAlias.ACADEMIC_SKILL_CODE));
        Iterator iter= academicSkillBean.getList(criteria).iterator();
        while(iter.hasNext()){
        	AcademicSkill academicSkill = (AcademicSkill)iter.next();
            SelectItem item = new SelectItem(academicSkill.getId(), academicSkill.getCode() +" / "+ academicSkill.getDescription());
            academicSkills.add(item);
        }
        return academicSkills;
    }

	@SuppressWarnings("unchecked")
    public List<SelectItem> getMarkSubjects() throws ManagerBeanException{
        List<SelectItem> markSubjects = new LinkedList<SelectItem>();
        try{
            CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
            Course course = (Course)courseController.getTo();

            IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), course.getId());
            Iterator iter= courseAcademicSkillBean.getList(criteria).iterator();
            while(iter.hasNext()){
            	CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)iter.next();
                SelectItem item = new SelectItem(courseAcademicSkill.getId(), courseAcademicSkill.getAcademicSkill().getDescription());
                markSubjects.add(item);
            }
            return markSubjects;
        }catch (Exception e) {
        	throw new ManagerBeanException(e);
		}
    }
    
	@SuppressWarnings("unchecked")
    public List<SelectItem> getMarkAlumns() throws ManagerBeanException{
        List<SelectItem> markAlumns = new LinkedList<SelectItem>();
        try{
            CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
            Course course = (Course)courseController.getTo();

            IManagerBean courseAlumnBean = BeanManager.getManagerBean(CourseAlumn.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(courseAlumnBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
            Iterator iter= courseAlumnBean.getList(criteria).iterator();
            while(iter.hasNext()){
            	CourseAlumn courseAlumn = (CourseAlumn)iter.next();
                SelectItem item = new SelectItem(courseAlumn.getCustomer().getId(), courseAlumn.getCustomer().getRegistry().getName());
                markAlumns.add(item);
            }
            return markAlumns;
        }catch (Exception e) {
        	throw new ManagerBeanException(e);
		}
    }

	public List<SelectItem> getCourseAlumnStatuses() {
		List<SelectItem> courseAlumnStatuses = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		CourseAlumnStatus[] statuses = CourseAlumnStatus.values();
		for (int i = 0; i < statuses.length; i++) {
			CourseAlumnStatus status = statuses[i];
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			courseAlumnStatuses.add(item);
		}
		return courseAlumnStatuses;
	}

	@SuppressWarnings("unchecked")
    public List<SelectItem> getQualitySkills() throws ManagerBeanException{
        List<SelectItem> qualitySkills = new LinkedList<SelectItem>();
        IManagerBean qualitySkillBean = BeanManager.getManagerBean(QualitySkill.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(qualitySkillBean.getFieldName(IAcademyAlias.QUALITY_SKILL_CODE));
        Iterator iter= qualitySkillBean.getList(criteria).iterator();
        while(iter.hasNext()){
        	QualitySkill qualitySkill = (QualitySkill)iter.next();
            SelectItem item = new SelectItem(qualitySkill.getId(), qualitySkill.getCode() +" / "+ qualitySkill.getDescription());
            qualitySkills.add(item);
        }
        return qualitySkills;
    }
    */

}