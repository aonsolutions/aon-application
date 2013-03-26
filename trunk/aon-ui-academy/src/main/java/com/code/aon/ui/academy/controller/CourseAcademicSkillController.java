package com.code.aon.ui.academy.controller;

import static com.code.aon.ui.academy.controller.IAcademyConstants.COURSE_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseAcademicSkillController extends LinesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CourseAcademicSkillController.class);
	
	private Integer getCourseId() {
		IController courseController = FormUtil.getController(COURSE_CONTROLLER_NAME);
		return ((Course)courseController.getTo()).getId();		
	}
	
	public Integer getWeightSum() {
		Integer weightSum = 0;
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IEntityAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), getCourseId());
			for( ITransferObject to : courseAcademicSkillBean.getList(criteria) ) {
				CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill) to;
				weightSum += courseAcademicSkill.getWeight();
			}
		} catch(ManagerBeanException e) {
			LOGGER.error("Error getting courseAcademicSkill list", e);
		}

		return weightSum;
	}

	private List<AcademicSkill> getCurrentAcademicSkills() throws ManagerBeanException {
		List<AcademicSkill> academicSkills = new LinkedList<AcademicSkill>();
        IManagerBean bean = BeanManager.getManagerBean(CourseAcademicSkill.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), getCourseId());
		for( ITransferObject to : bean.getList(criteria) ) {
			academicSkills.add( ((CourseAcademicSkill) to).getAcademicSkill() );
        }
        return academicSkills;		
	}
	
	public List<SelectItem> getSelectableAcademicSkills() throws ManagerBeanException {
        List<SelectItem> academicSkills = new LinkedList<SelectItem>();
        List<AcademicSkill> currentAcademicSkills = getCurrentAcademicSkills();
        IManagerBean academicSkillBean = BeanManager.getManagerBean(AcademicSkill.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(academicSkillBean.getFieldName(IEntityAlias.ACADEMIC_SKILL_CODE));
		for( ITransferObject to : academicSkillBean.getList(criteria) ) {
			AcademicSkill academicSkill = (AcademicSkill) to;
			if (! currentAcademicSkills.contains(academicSkill) ) {
	            SelectItem item = new SelectItem(academicSkill, academicSkill.getCode() +" / "+ academicSkill.getDescription());
	            academicSkills.add(item);				
			}
        }
        return academicSkills;
	}
	
}
