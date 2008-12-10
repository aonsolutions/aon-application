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
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the finance maintenance.
 * 
 */
public class FinanceController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(FinanceController.class.getName());
	
	/**
	 * A list of finances currently checked
	 */
	private ArrayList<Finance> checks= new ArrayList<Finance>();
	
	private RegistryBank registryBank;
	
	private Date paymentDate;
	
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}
	
	public void onRegistryBankChanged( ValueChangeEvent event ) {
		Finance finance = (Finance) getTo();
		RegistryBank bank = (RegistryBank) event.getNewValue();
		finance.setBankAccount( bank != null ? bank.getBankAccount() : null );
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
	
	@SuppressWarnings("unchecked")
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			if (!checks.contains( finance )) {
				checks.add( finance );
			}
		}
	}

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

	@SuppressWarnings("unchecked")
	public List getRegistryBanks(){
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		Invoice invoice = ((Finance)this.getTo()).getInvoice(); 
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), invoice.getRegistry().getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank, rBank.getBank().getName());
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks for registry with id=" + invoice.getRegistry().getId(), e);
		}
		return rBanks;
	}
}