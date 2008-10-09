package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * This controller checks finances, sales and purchases. 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class FinanceCheckingController extends GridController {
	
	/**
	 * The logger of the class
	 */
	private static final Logger LOGGER = Logger.getLogger(FinanceCheckingController.class.getName());
	
	/**
	 * Sel sales(false) or purchase(true) mode
	 */
	private Boolean payment;
	
	/**
	 * Is pending
	 */
	private Boolean pending;
	
	/**
	 * Is payment
	 * 
	 * @return payment
	 */
	public Boolean getPayment() {
		return payment;
	}

	/**
	 * Assigns if is payment
	 * 
	 * @param payment is payment
	 */
	public void setPayment(Boolean payment) {
		this.payment = payment;
	}

	/**
	 * Returns if is pending
	 * 
	 * @return is pending
	 */
	public Boolean getPending() {
		return pending;
	}

	/**
	 * Set if is pending
	 * 
	 * @param pending is pending
	 */
	public void setPending(Boolean pending) {
		this.pending = pending;
	}
	
	/**
	 * Assigns payment value, true for purchase
	 * 
	 * @param event the menu event
	 */
	@SuppressWarnings("unused")	
	public void setPurchaseMode(MenuEvent event){
		try {
			this.clearCriteria();
			this.payment = new Boolean(true);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error setting purchase mode", e);
		}
	}
	
	/**
	 * Assigns payment value, false for sales
	 * 
	 * @param event the menu event
	 */
	@SuppressWarnings("unused")	
	public void setSalesMode(MenuEvent event){
		try {
			this.clearCriteria();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error setting sales mode", e);
		}
		this.payment = new Boolean(false);
	}
	
	/**
	 * Initialices the controller, pending is null, and searchs
	 * 
	 * @see com.code.aon.ui.form.BasicController#onEditSearch(javax.faces.event.ActionEvent)
	 */
	@Override
	public void onEditSearch(ActionEvent event) {
		try {
			this.clearCriteria();
			this.pending = null;
			super.onEditSearch(event);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error reseting criteria", e);
		}
	}

	/**
	 * If pending, marks as paid else mark as pending the checklist
	 * 
	 * @param event an action event
	 */
	@SuppressWarnings("unused")
	public void onMark(ActionEvent event){
		if(this.getPending().booleanValue()){
			markAsPaid(getCheckList());
		}else{
			markAsPending(getCheckList());
		}
		this.onEditSearch(null);
	}
	

	/**
	 * Iterate all the finance of the checklist, set status as paid and updates the invoice
	 * 
	 * @param checkList list of Finance
	 */
	@SuppressWarnings("unchecked")
	private void markAsPaid(ArrayList<ITransferObject> checkList) {
		Iterator iter = checkList.iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			finance.setFinanceStatus(FinanceStatus.PAID);
			try {
				getManagerBean().update(finance);
			} catch (ManagerBeanException e) {
				
			}
		}
	}

	/**
	 * Iterate all the finance of the checklist, set status as pending and updates the invoice
	 * 
	 * @param checkList list of Finance
	 */
	@SuppressWarnings("unchecked")
	private void markAsPending(ArrayList<ITransferObject> checkList) {
		Iterator iter = checkList.iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			finance.setFinanceStatus(FinanceStatus.PENDING);
			try {
				getManagerBean().update(finance);
			} catch (ManagerBeanException e) {
				
			}
		}
	}
	
	/**
	 * Adds to criteria the payment
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addPaymentExpression(ValueChangeEvent event) throws ManagerBeanException{
		Criteria criteria = getCriteria();
		criteria.addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.FINANCE_PAYMENT), event.getNewValue());
		setCriteria(criteria);
	}
	
	/**
	 * Adds to criteria the finance status
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		Criteria criteria = getCriteria();
		FinanceStatus status = (((Boolean)event.getNewValue()).booleanValue()?FinanceStatus.PENDING:FinanceStatus.PAID);
		criteria.addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), status);
		setCriteria(criteria);
	}
	
	/**
	 * Adds to criteria the finance date. Greater than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addIssuedate1Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_DUE_DATE), value);
			setCriteria(c);
		}
	}

	/**
	 * Adds to criteria the finance date. Less than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addIssuedate2Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_DUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to criteria the registry ident
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	public void addRegistryExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		if(event.getNewValue() != null && !event.getNewValue().equals("")) {
			Criteria c = getCriteria();
			c.addExpression(getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), event.getNewValue().toString());
			setCriteria(c);
		}
	}
	
}
