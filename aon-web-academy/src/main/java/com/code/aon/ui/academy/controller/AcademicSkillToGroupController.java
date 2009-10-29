package com.code.aon.ui.academy.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.AcademicSkill;
import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAcademicSkill;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.menu.jsf.MenuManager;
import com.code.aon.ui.util.AonUtil;

public class AcademicSkillToGroupController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AcademicSkillToGroupController.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";

	private static final String MENU_MANAGER_NAME = "menuManager";

	private ArrayList<Course> checks = new ArrayList<Course>();
	
	private Integer skillId;
	
	private Integer weight;
	
	public Integer getSkillId() {
		return skillId;
	}

	public void setSkillId(Integer skillId) {
		this.skillId = skillId;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	@SuppressWarnings({"unused","unchecked"})
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Course detail = (Course)iter.next();
			if (!checks.contains( detail )) {
				checks.add( detail );
			}
		}
	}

	@SuppressWarnings("unused")
	public void checkNone(ActionEvent event) {
		clearCheckedCourses();
	}

	public boolean getRowChecked() {
		Course to = (Course) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Course to = (Course) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Course to = (Course) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public void clearCheckedCourses() {
		checks = new ArrayList<Course>();
	}
	
	public void onEditSearch(MenuEvent event){
		this.onEditSearch((ActionEvent)event);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setWeight(0);
		clearCheckedCourses();
		super.onEditSearch(event);
		try {
			getCriteria().addOrder(getManagerBean().getFieldName(IAcademyAlias.COURSE_CODE));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to apply criteria");
			LOGGER.log(Level.SEVERE, "Unable to apply criteria", e);
			throw new AbortProcessingException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onAssign(ActionEvent event){
		try {
			CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			if(getSkillId() != null && checks.size() > 0){
				if(validateCourseAcademicSkills()) {
					IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
					AcademicSkill skill = obtainAcademicSkill();
					Iterator iter = checks.iterator();
					while(iter.hasNext()){
						Course course = (Course)iter.next();
						criteria.addOrExpression(courseController.getFieldName(IAcademyAlias.COURSE_ID), course.getId().toString());
						CourseAcademicSkill courseAcademicSkill = new CourseAcademicSkill();
						courseAcademicSkill.setAcademicSkill(skill);
						courseAcademicSkill.setCourse(course);
						courseAcademicSkill.setWeight((getWeight() != null) ? getWeight() : 0);
						courseAcademicSkillBean.insert(courseAcademicSkill);
					}
				}
			}
			courseController.setCriteria(criteria);
			courseController.onSearch(null);
			updateBreadCrumb();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to assign the academic skill to selected courses");
			LOGGER.log(Level.SEVERE, "Unable to assign the academic skill to selected courses", e);
			throw new AbortProcessingException(e);
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage("Unable to assign the academic skill to selected courses");
			LOGGER.log(Level.SEVERE, "Unable to assign the academic skill to selected courses", e);
			throw new AbortProcessingException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean validateCourseAcademicSkills() {
		boolean valid = true;
		try {
			IManagerBean courseAcademicSkillBean = BeanManager.getManagerBean(CourseAcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_ACADEMIC_SKILL_ID), getSkillId());
			Expression selectedCoursesExp = null;
			Iterator checkedIter = checks.iterator();
			while(checkedIter.hasNext()){
				Course course = (Course)checkedIter.next();
				selectedCoursesExp = ExpressionUtilities.getOrExpression(selectedCoursesExp, ExpressionUtilities.getEqualExpression(courseAcademicSkillBean.getFieldName(IAcademyAlias.COURSE_ACADEMIC_SKILL_COURSE_ID), course.getId()));
			}
			criteria.addExpression(selectedCoursesExp);
			List courseAcademicSkills = courseAcademicSkillBean.getList(criteria);
			valid = courseAcademicSkills.size() == 0;
			Iterator iter = courseAcademicSkills.iterator();
			String message = "Selected Academic Skill was already included in next courses:";
			while(iter.hasNext()){
				CourseAcademicSkill courseAcademicSkill = (CourseAcademicSkill)iter.next();
				message += " " + courseAcademicSkill.getCourse().getCode();
			}
			if(!valid){
				AonUtil.addErrorMessage(message);
				LOGGER.log(Level.SEVERE, message);
				throw new AbortProcessingException(message);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			throw new AbortProcessingException(e);
		}
		return valid;
	}

	private void updateBreadCrumb() {
		MenuManager menuManager = (MenuManager)AonUtil.getRegisteredBean(MENU_MANAGER_NAME);
        menuManager.setCurrentMenu("AON_APP");
        menuManager.getCurrentMenuModel().setSelectedNode("root.aon_course");	
    }

	@SuppressWarnings("unchecked")
	private AcademicSkill obtainAcademicSkill() {
		try {
			IManagerBean academicSkillBean = BeanManager.getManagerBean(AcademicSkill.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(academicSkillBean.getFieldName(IAcademyAlias.ACADEMIC_SKILL_ID), getSkillId());
			Iterator iter = academicSkillBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				return (AcademicSkill)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining academic skill with id = " + skillId, e);
		}
		return null;
	}
}