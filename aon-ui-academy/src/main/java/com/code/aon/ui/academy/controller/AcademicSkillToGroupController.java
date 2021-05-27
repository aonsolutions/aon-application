package com.code.aon.ui.academy.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AcademicSkillToGroupController extends GroupSelectionController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AcademicSkillToGroupController.class);
	
	private AcademicSkill academicSkill;
	
	private Integer weight;
	
	public AcademicSkill getAcademicSkill() {
		return academicSkill;
	}

	public void setAcademicSkill(AcademicSkill academicSkill) {
		this.academicSkill = academicSkill;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setWeight(0);
		try {
			setAcademicSkill((AcademicSkill)BeanManager.getManagerBean(AcademicSkill.class).createNewTo());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
	}
	
	public void onAssign(ActionEvent event){
		try {
			if( getAcademicSkill().getId() != null ) {
				if(validateCourseAcademicSkills()) {
					IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
					IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
					for( Serializable id : getCheckList() ) {
						Course course = (Course) courseBean.get(id);
						CourseAcademicSkill courseAcademicSkill = new CourseAcademicSkill();
						courseAcademicSkill.setAcademicSkill(getAcademicSkill());
						courseAcademicSkill.setCourse(course);
						courseAcademicSkill.setWeight((getWeight() != null) ? getWeight() : 0);
						courseAcademicSkillBean.insert(courseAcademicSkill);
					}
				}
			}
			updateCourseController(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to assign the academic skill to selected courses");
			LOGGER.error("Unable to assign the academic skill to selected courses", e);
			throw new AbortProcessingException(e);
		}
	}

	private boolean validateCourseAcademicSkills() {
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IEntityAlias.COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL_ID), getAcademicSkill().getId());
			criteria.addInExpression(courseAcademicSkillBean.getFieldName(IEntityAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), getCheckList());
			List<ITransferObject> courseAcademicSkills = courseAcademicSkillBean.getList(criteria);
			if (! courseAcademicSkills.isEmpty() ) {
				String message = "Selected Academic Skill was already included in next courses:";
				for( ITransferObject to : courseAcademicSkills ) {
					CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill) to;
					message += " " + courseAcademicSkill.getCourse().getCode();					
				}
				AonUtil.addErrorMessage(message);
				LOGGER.error(message);
				throw new AbortProcessingException(message);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.error(e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
		return true;
	}

}