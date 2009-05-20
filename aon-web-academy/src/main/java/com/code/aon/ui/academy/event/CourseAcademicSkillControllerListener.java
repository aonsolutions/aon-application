package com.code.aon.ui.academy.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CourseAcademicSkillControllerListener extends ControllerAdapter {

	private static final Logger LOGGER = Logger.getLogger(CourseAcademicSkillControllerListener.class.getName());

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		((CourseAcademicSkill)event.getController().getTo()).setWeight(0);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)event.getController().getTo();
		if(existingAcademicSkill(courseAcademicSkill)){
			AonUtil.addErrorMessage("No se puede añadir la misma habilidad dos veces en un curso");
			throw new AbortProcessingException();
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)event.getController().getTo();
		if(existingAcademicSkill(courseAcademicSkill)){
			AonUtil.addErrorMessage("No se puede añadir la misma habilidad dos veces en un curso");
			throw new AbortProcessingException();
		}
	}

	private boolean existingAcademicSkill(CourseAcademicSkill courseAcademicSkill) {
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			if (courseAcademicSkill.getId() != null) {
				criteria.addExpression(ExpressionUtilities.getNotEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_ID), courseAcademicSkill.getId()));
			}
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), courseAcademicSkill.getCourse().getId());
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL_ID), courseAcademicSkill.getAcademicSkill().getId());
			if (courseAcademicSkillBean.getCount(criteria) > 0) {
				return true;
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error checking if courseAcademicSkill exists", e);
		}
		return false;
	}
}