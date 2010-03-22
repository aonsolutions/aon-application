package com.code.aon.ui.academy.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.sales.CustomerFee;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.menu.jsf.MenuManager;
import com.code.aon.ui.util.AonUtil;

public class CustomerFeeToGroupController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(AcademicSkillToGroupController.class.getName());
	
	private static final String COURSE_CONTROLLER_NAME = "course";

	private static final String MENU_MANAGER_NAME = "menuManager";

	private ArrayList<Course> checks = new ArrayList<Course>();
	
	private CustomerFee fee;
	
	public CustomerFee getFee() {
		return fee;
	}

	public void setFee(CustomerFee fee) {
		this.fee = fee;
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
	
	@Override
	public void onSearch(ActionEvent event) {
		initializeFee();
		super.onSearch(event);
	}
	
	@SuppressWarnings("unchecked")
	public void onAssign(ActionEvent event){
		try {
			CourseController courseController = (CourseController)AonUtil.getController(COURSE_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			if(fee.getQuantity() <= 0){
				String message = "Quantity must be > 0";
				AonUtil.addErrorMessage(message);
				LOGGER.log(Level.SEVERE, message);
				throw new AbortProcessingException(message);
			} else if(checks.size() > 0){
				Iterator iter = checks.iterator();
				while(iter.hasNext()){
					Course course = (Course)iter.next();
					criteria.addOrExpression(courseController.getFieldName(IAcademyAlias.COURSE_ID), course.getId().toString());
					createCustomerFeesToCourse(course);
				}
			}
			courseController.setCriteria(criteria);
			courseController.onSearch(null);
			updateBreadCrumb();
		} catch (ManagerBeanException e) {
			String message = "Unable to add fee to selected courses";
			AonUtil.addErrorMessage(message);
			LOGGER.log(Level.SEVERE, message, e);
			throw new AbortProcessingException(e);
		} catch (ExpressionException e) {
			String message = "Unable to add fee to selected courses";
			AonUtil.addErrorMessage(message);
			LOGGER.log(Level.SEVERE, message, e);
			throw new AbortProcessingException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void createCustomerFeesToCourse(Course course) throws ManagerBeanException {
		IManagerBean courseAlumBean = BeanManager.getManagerBean(CourseAlumn.class);
		IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(courseAlumBean.getFieldName(IAcademyAlias.COURSE_ALUMN_COURSE_ID), course.getId());
		Iterator iter = courseAlumBean.getList(criteria).iterator();
		fee.setWorkPlace(course.getWorkPlace());
		while(iter.hasNext()){
			CourseAlumn courseAlumn = (CourseAlumn)iter.next();
			fee.setCustomer(courseAlumn.getCustomer());
			customerFeeBean.insert(fee);
		}
	}

	private void initializeFee() {
		fee = new CustomerFee();
		fee.setItem(new Item());
		fee.getItem().setProduct(new Product());
		fee.setCustomer(null);
		fee.setDescription("");
		fee.setDiscountExpression(new DiscountExpression("0.0"));
		fee.setWorkPlace(null);
		fee.setInitialDate(new Date());
		fee.setBillingDate(new Date());
		fee.setSecurityLevel(SecurityLevel.OFFICIAL);
	}

	private void updateBreadCrumb() {
		MenuManager menuManager = (MenuManager)AonUtil.getRegisteredBean(MENU_MANAGER_NAME);
        menuManager.setCurrentMenu("AON_APP");
        menuManager.getCurrentMenuModel().setSelectedNode("root.aon_course");	
    }
}