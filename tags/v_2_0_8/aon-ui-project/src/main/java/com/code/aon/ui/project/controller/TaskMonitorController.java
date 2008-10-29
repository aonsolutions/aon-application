package com.code.aon.ui.project.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class TaskMonitorController extends BasicController implements ITaskController {
	
	private static final Logger LOGGER = Logger.getLogger(TaskMonitorController.class.getName());

	private Customer customer;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public void onEditSearch(MenuEvent event){
		super.onEditSearch(new ActionEvent(event.getComponent()));
	}
	
	public void addStartDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IProjectAlias.TASK_START_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM start date expression", e);
            }
        }
    }
    
    public void addStartDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IProjectAlias.TASK_START_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO start date expression", e);
            }
        }
    }

    public void addEndDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IProjectAlias.TASK_END_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM end date expression", e);
            }
        }
    }
    
    public void addEndDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IProjectAlias.TASK_END_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO end date expression", e);
            }
        }
    }

    public void addDueDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IProjectAlias.TASK_DUE_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM due date expression", e);
            }
        }
    }
    
    public void addDueDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IProjectAlias.TASK_DUE_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO due date expression", e);
            }
        }
    }

    public void addCustomerExpression(ValueChangeEvent event) {
        if(event.getNewValue() != null && !event.getNewValue().equals("")) {
            try {
                getCriteria().addEqualExpression(getFieldName(IProjectAlias.TASK_CUSTOMER_ID), new Integer(event.getNewValue().toString()));
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding customer expression", e);
            }
        }
    }
    
    public void addStatusExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addEqualExpression(getFieldName(IProjectAlias.TASK_STATUS), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding status expression", e);
            }
        }
    }
}