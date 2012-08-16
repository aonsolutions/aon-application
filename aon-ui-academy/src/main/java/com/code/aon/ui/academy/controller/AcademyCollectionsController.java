package com.code.aon.ui.academy.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.academy.AcademicSkill;
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

    public AcademicSkill getAcademicSkill() {
		return null;
	}

	public void setAcademicSkill(AcademicSkill academicSkill) {
	}
	
    public List<SelectItem> getAcademicSkills() throws ManagerBeanException{
        List<SelectItem> academicSkills = new LinkedList<SelectItem>();
        IManagerBean academicSkillBean = BeanManager.getManagerBean(AcademicSkill.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(academicSkillBean.getFieldName(IEntityAlias.ACADEMIC_SKILL_CODE));
		for( ITransferObject to : academicSkillBean.getList(criteria) ) {
			AcademicSkill academicSkill = (AcademicSkill) to;
            SelectItem item = new SelectItem(academicSkill, academicSkill.getCode() +" / "+ academicSkill.getDescription());
            academicSkills.add(item);
        }
        return academicSkills;
    }

}