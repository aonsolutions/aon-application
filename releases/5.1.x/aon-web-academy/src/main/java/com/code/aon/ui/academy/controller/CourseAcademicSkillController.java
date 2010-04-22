package com.code.aon.ui.academy.controller;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CourseAcademicSkillController extends LinesController {

	private static final String COURSE_CONTROLLER_NAME = "course";

	private static final Logger LOGGER = Logger.getLogger(CourseAcademicSkillController.class.getName());

	public void onAcademicSkillChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean academicSkillBean = BeanManager.getManagerBean(AcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(academicSkillBean.getFieldName(IAcademyAlias.ACADEMIC_SKILL_ID), event.getNewValue());
		}
	}

	@SuppressWarnings("unchecked")
	public Integer getWeightSum() {
		CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
		Integer courseId = ((Course)courseController.getTo()).getId();
		Integer weightSum = 0;
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), courseId);
			Iterator iterator = courseAcademicSkillBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)iterator.next();
				weightSum += courseAcademicSkill.getWeight();
			}
		} catch(ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting courseAcademicSkill list", e);
		}

		return weightSum;
	}

}
