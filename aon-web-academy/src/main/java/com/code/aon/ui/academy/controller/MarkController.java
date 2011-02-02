package com.code.aon.ui.academy.controller;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.Mark;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class MarkController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(MarkController.class.getName());

	public void onNewSearch(MenuEvent menuevent){
		onEditSearch(null);
	}
	
	public void updateCriteria(CourseAlumn courseAlumn, int evaluation) throws ManagerBeanException{
		try{
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(this.getFieldName(IAcademyAlias.MARK_ALUMN_ID),courseAlumn==null?new Integer(-1):courseAlumn.getId());
			criteria.addEqualExpression(this.getFieldName(IAcademyAlias.MARK_EVALUATION),new Integer(evaluation));
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria courseAcademicSkillCriteria = new Criteria();
			Course course = courseAlumn==null?null:courseAlumn.getCourse(); 
			courseAcademicSkillCriteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), course==null?new Integer(-1):course.getId());
			List<ITransferObject> listCourseAcademicSkill = courseAcademicSkillBean.getList(courseAcademicSkillCriteria);
			if (listCourseAcademicSkill.isEmpty()){
				criteria.addExpression(this.getFieldName(IAcademyAlias.MARK_SUBJECT_ID),String.valueOf("-1"));
			}else{
				Iterator<ITransferObject> iterCourseAcademicSkill = listCourseAcademicSkill.iterator();
				Expression orExpression = null;
				while (iterCourseAcademicSkill.hasNext()){
					CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)iterCourseAcademicSkill.next();
					if (orExpression == null){
						orExpression = ExpressionUtilities.getEqualExpression(this.getFieldName(IAcademyAlias.MARK_SUBJECT_ID),courseAcademicSkill.getId());
					}else{
						Expression expr2 = ExpressionUtilities.getEqualExpression(this.getFieldName(IAcademyAlias.MARK_SUBJECT_ID),courseAcademicSkill.getId());
						orExpression = ExpressionUtilities.getOrExpression(orExpression, expr2);
					}
				}
				criteria.addExpression(orExpression);
			}
			criteria.addOrder(this.getFieldName(IAcademyAlias.MARK_EVALUATION));
			criteria.addOrder(this.getFieldName(IAcademyAlias.MARK_ALUMN_ID));
			criteria.addOrder(this.getFieldName(IAcademyAlias.MARK_SUBJECT_ID));
			this.setCriteria(criteria);
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Double getAverageMark() throws ManagerBeanException {
		boolean printAverage = false;
		Double averageMark = 0.0;
		Double weightSum = 0.0;
		try {
			Iterator iterator = getManagerBean().getList(getCriteria()).iterator();
			while (iterator.hasNext()) {
				Mark mark = (Mark)iterator.next();
				if (mark.getMark() != null) {
					printAverage = true;
					averageMark += mark.getMark() * mark.getSubject().getWeight();
					weightSum += mark.getSubject().getWeight();
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting mark list", e);
		}
		return (!printAverage || weightSum == 0)?null:round(averageMark / weightSum, 2);
	}

	private double round(double value, int precision) {
		return Math.round(value * Math.pow(10, precision)) / Math.pow(10, precision);
	}

}