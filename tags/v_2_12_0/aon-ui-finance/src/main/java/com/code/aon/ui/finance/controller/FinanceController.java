package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

/**
 * Controller used in the finance maintenance.
 * 
 */
public class FinanceController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(FinanceController.class.getName());
	
	/** Determines if the finance is a payment or a charge. */
	private Boolean payment;
	
	/**
	 * A list of finances currently checked
	 */
	private ArrayList<Finance> checks= new ArrayList<Finance>();
	
	private Integer registryBankId;
	
	private Date paymentDate;
	
	/**
	 * Gets if the finance is a payment or a charge.
	 * 
	 * @return true if the finance is a payment
	 */
	public Boolean getPayment() {
		return payment;
	}

	/**
	 * Sets if the finance is a payment or a charge.
	 * 
	 * @param payment true if the finance is a payment
	 */
	public void setPayment(Boolean payment) {
		this.payment = payment;
	}
	
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Integer getRegistryBankId() {
		return registryBankId;
	}

	public void setRegistryBankId(Integer registryBankId) {
		this.registryBankId = registryBankId;
	}

	/**
	 * If the value changed sets the row to be checked or not.
	 * true to add or false to remove
	 * 
	 * @param event the event that is launched by value change
	 */
	public void rowSelected(ValueChangeEvent event){
		if(event.getNewValue() != null){
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	@SuppressWarnings({"unused","unchecked"})
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			if (!checks.contains( finance )) {
				checks.add( finance );
			}
		}
	}

	@SuppressWarnings("unused")
	public void checkNone(ActionEvent event) {
		clearCheckedFinances();
	}

	/**
	 * Determines if the current row is selected or not
	 * 
	 * @return true if the current row is selected
	 */
	public boolean getRowChecked() {
		Finance to = (Finance) model.getRowData();
		return checks.contains( to );
	}
	
	/**
	 * Adds or removes a Delivery in the checks list
	 * 
	 * @param rowChecked true to add and false to remove
	 */
	public void setRowChecked(boolean rowChecked) {
		if ( rowChecked ) {
			Finance to = (Finance) model.getRowData();
			if (!checks.contains( to )) {
				checks.add( to );
			}
		} else {
			Finance to = (Finance) model.getRowData();
			if (checks.contains( to )) {
				checks.remove( to );
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinances() {
		return checks;
	}
	
	public void clearCheckedFinances(){
		checks = new ArrayList<Finance>();
	}
	
    public boolean isPending() {
    	return ((Finance)this.getTo()).getFinanceStatus().equals(FinanceStatus.PENDING);
    }
    
    public boolean isReturned(){
    	return ((Finance)this.getTo()).getFinanceStatus().equals(FinanceStatus.RETURNED);
    }

	public void onEditSearch(MenuEvent event) throws ManagerBeanException {
		this.onEditSearch((ActionEvent)event);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setPayment(new Boolean(false));
		super.onEditSearch(event);
	}

	/**
	 * Adds to criteria the finance status.
	 * 
	 * @param event the event that contains the new value
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		Criteria criteria = getCriteria();
		FinanceStatus status = (((Boolean)event.getNewValue()).booleanValue()?FinanceStatus.PENDING:FinanceStatus.PAID);
		criteria.addEqualExpression(getManagerBean().getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), status);
		setCriteria(criteria);
	}
	
	/**
	 * Adds to criteria the registry ident.
	 * 
	 * @param event the event that contains the new value
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 * @throws ExpressionException the expression exception
	 */
	public void addRegistryExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		if(event.getNewValue() != null && !event.getNewValue().equals("")) {
			Criteria c = getCriteria();
			c.addExpression(getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), event.getNewValue().toString());
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to criteria the invoice issueDate. Greater than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addInvoiceIssueDate1Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}

	/**
	 * Adds to criteria the finance dueDate. Greater than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addFinanceDueDate1Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
			Object value = event.getNewValue();
			Criteria c = getCriteria();
			c.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_DUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to criteria the invoice issueDate. Less than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addInvoiceIssueDate2Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
			Object value = event.getNewValue();
			Criteria c = getCriteria();
			c.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_INVOICE_ISSUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to criteria the finance dueDate. Less than or equal
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 */
	public void addFinanceDueDate2Expression(ValueChangeEvent event) throws ManagerBeanException {
	    if (event.getNewValue() != null) {
			Object value = event.getNewValue();
			Criteria c = getCriteria();
			c.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.FINANCE_DUE_DATE), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds to criteria the bank.
	 * 
	 * @param event the event that contains the new value
	 * @throws ManagerBeanException
	 * @throws ExpressionException 
	 */
	public void addBankExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
	    if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Object value = event.getNewValue();
			Criteria c = getCriteria();
			c.addExpression(getFieldName(IFinanceAlias.FINANCE_BANK_ID), value.toString());
			setCriteria(c);
		}
	}

	@SuppressWarnings("unchecked")
	public List getRegistryBanks(){
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		Invoice invoice = ((Finance)this.getTo()).getInvoice(); 
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), invoice.getRegistry().getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank.getId(), rBank.getBank().getName());
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks for registry with id=" + invoice.getRegistry().getId(), e);
		}
		return rBanks;
	}
}