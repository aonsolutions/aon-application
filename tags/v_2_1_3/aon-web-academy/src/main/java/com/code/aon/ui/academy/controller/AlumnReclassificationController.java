package com.code.aon.ui.academy.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.CustomerSegment;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.menu.jsf.MenuManager;
import com.code.aon.ui.util.AonUtil;

public class AlumnReclassificationController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AlumnReclassificationController.class.getName());
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	private static final String MENU_MANAGER_NAME = "menuManager";

	private ArrayList<Customer> checks = new ArrayList<Customer>();
	
	private Integer customerSegmentId;
	
	private CustomerStatus customerStatus;
	
	public Integer getCustomerSegmentId() {
		return customerSegmentId;
	}

	public void setCustomerSegmentId(Integer customerSegmentId) {
		this.customerSegmentId = customerSegmentId;
	}

	public CustomerStatus getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CustomerStatus customerStatus) {
		this.customerStatus = customerStatus;
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
			Customer detail = (Customer)iter.next();
			if (!checks.contains( detail )) {
				checks.add( detail );
			}
		}
	}

	@SuppressWarnings("unused")
	public void checkNone(ActionEvent event) {
		clearCheckedCustomers();
	}

	public boolean getRowChecked() {
		Customer to = (Customer) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Customer to = (Customer) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Customer to = (Customer) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public void clearCheckedCustomers() {
		checks = new ArrayList<Customer>();
	}
	
	public void onEditSearch(MenuEvent event){
		this.onEditSearch((ActionEvent)event);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		clearCheckedCustomers();
		super.onEditSearch(event);
		try {
			getCriteria().addOrder(getManagerBean().getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_SURNAME));
			getCriteria().addOrder(getManagerBean().getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_NAME));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to apply criteria");
			LOGGER.log(Level.SEVERE, "Unable to apply criteria", e);
			throw new AbortProcessingException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onAssign(ActionEvent event){
		try {
			CustomerController customerController = (CustomerController)AonUtil.getController(CUSTOMER_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			if(customerSegmentId != null && checks.size() > 0){
				IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
				CustomerSegment customerSegment = obtainCustomerSegment();
				Iterator iter = checks.iterator();
				while(iter.hasNext()){
					Customer customer = (Customer)iter.next();
					criteria.addOrExpression(customerController.getFieldName(ICustomerAlias.CUSTOMER_ID), customer.getId().toString());
					customer.setCustomerSegment(customerSegment);
					customer.setStatus(customerStatus);
					customerBean.update(customer);
				}
			}
			customerController.setCriteria(criteria);
			customerController.onSearch(null);
			updateBreadCrumb();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Unable to assign segment or status to selected alumns");
			LOGGER.log(Level.SEVERE, "Unable to assign segment or status to selected alumns", e);
			throw new AbortProcessingException(e);
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage("Unable to assign segment or status to selected alumns");
			LOGGER.log(Level.SEVERE, "Unable to assign segment or status to selected alumns", e);
			throw new AbortProcessingException(e);
		}
	}

	private void updateBreadCrumb() {
		MenuManager menuManager = (MenuManager)AonUtil.getRegisteredBean(MENU_MANAGER_NAME);
        menuManager.setCurrentMenu("AON_APP");
        menuManager.getCurrentMenuModel().setSelectedNode("root.aon_customer");	
    }

	@SuppressWarnings("unchecked")
	private CustomerSegment obtainCustomerSegment() {
		try {
			IManagerBean customerSegmentBean = BeanManager.getManagerBean(CustomerSegment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerSegmentBean.getFieldName(ICustomerAlias.CUSTOMER_SEGMENT_ID), customerSegmentId);
			Iterator iter = customerSegmentBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				return (CustomerSegment)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining customer segment with id = " + customerSegmentId, e);
		}
		return null;
	}
}