package com.code.aon.ui.academy.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.Course;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.academy.enumeration.CourseStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.menu.jsf.MenuManager;
import com.code.aon.ui.util.AonUtil;

public class CloseGroupController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(CloseGroupController.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";

	private static final String MENU_MANAGER_NAME = "menuManager";

	private ArrayList<Course> checks = new ArrayList<Course>();
	
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
		clearCheckedCourses();
		super.onEditSearch(event);
		try {
			getCriteria().addEqualExpression(getManagerBean().getFieldName(IAcademyAlias.COURSE_STATUS), CourseStatus.ACTIVE);
			getCriteria().addOrder(getManagerBean().getFieldName(IAcademyAlias.COURSE_CODE));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to apply criteria");
			LOGGER.log(Level.SEVERE, "Unable to apply criteria", e);
			throw new AbortProcessingException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onClose(ActionEvent event){
		try {
			CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
			Iterator iter = checks.iterator();
			while(iter.hasNext()){
				Course course = (Course)iter.next();
				criteria.addOrExpression(courseController.getFieldName(IAcademyAlias.COURSE_ID), course.getId().toString());
				course.setStatus(CourseStatus.INACTIVE);
				courseBean.update(course);
			}
			courseController.setCriteria(criteria);
			courseController.onSearch(null);
			updateBreadCrumb();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to close the selected courses");
			LOGGER.log(Level.SEVERE, "Unable to close the selected courses", e);
			throw new AbortProcessingException(e);
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage("Unable to close the selected courses");
			LOGGER.log(Level.SEVERE, "Unable to close the selected courses", e);
			throw new AbortProcessingException(e);
		}
	}

	private void updateBreadCrumb() {
		MenuManager menuManager = (MenuManager)AonUtil.getRegisteredBean(MENU_MANAGER_NAME);
        menuManager.setCurrentMenu("AON_APP");
        menuManager.getCurrentMenuModel().setSelectedNode("root.aon_course");	
    }

	public void addStartDateFromExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
				getCriteria().addGreaterThanOrEqualExpression(courseBean.getFieldName(IAcademyAlias.COURSE_START_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
			}
		}
	}
	
	public void addStartDateToExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
				getCriteria().addLessThanOrEqualExpression(courseBean.getFieldName(IAcademyAlias.COURSE_START_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}

	public void addEndDateFromExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
				getCriteria().addGreaterThanOrEqualExpression(courseBean.getFieldName(IAcademyAlias.COURSE_END_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
			}
		}
	}
	
	public void addEndDateToExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean courseBean = BeanManager.getManagerBean(Course.class);
				getCriteria().addLessThanOrEqualExpression(courseBean.getFieldName(IAcademyAlias.COURSE_END_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}
}