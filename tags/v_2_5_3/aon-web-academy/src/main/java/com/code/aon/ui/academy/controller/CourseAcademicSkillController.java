package com.code.aon.ui.academy.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class CourseAcademicSkillController extends LinesController {

	
	public void onAcademicSkillChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean academicSkillBean = BeanManager.getManagerBean(AcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(academicSkillBean.getFieldName(IAcademyAlias.ACADEMIC_SKILL_ID), event.getNewValue());
		}
	}
}
