package com.code.aon.ui.accounting.controller;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountEntryController.class.getName());
	
	private SpecialEntryControllerManager controllerManager;
	
	private SpecialEntryControllerManager getControllerManager() {
		if (controllerManager == null) {
			controllerManager = new SpecialEntryControllerManager();
			// Wizard de facturas
			controllerManager.register(AccountEntryType.SALES_INVOICE, "invoiceEntry");
			controllerManager.register(AccountEntryType.PURCHASE_INVOICE, "invoiceEntry");
			controllerManager.register(AccountEntryType.EXPENSE_INVOICE, "invoiceEntry");
			// Wizard de cobros y pagos.
			controllerManager.register(AccountEntryType.PAYMENT, "financeEntry");
			controllerManager.register(AccountEntryType.COLLECTION, "financeEntry");
			// Wizard de gastos sin IVA
			controllerManager.register(AccountEntryType.EXPENSES, "expenseEntry");
			// Wizard de nóminas
			controllerManager.register(AccountEntryType.SALARY, "salaryEntry");
			// Wizard de Gastos Seguridad Social.
			controllerManager.register(AccountEntryType.SOCIAL_INSURANCE, "socialInsuranceEntry");
			// Wizard de Creación de préstamos.
			controllerManager.register(AccountEntryType.LOAN, "loanEntry");
			// Wizard de Cuotas de préstamos.			
			controllerManager.register(AccountEntryType.LOAN_FEE, "loanFeeEntry");
			// Wizard de Creación de leasing.
			controllerManager.register(AccountEntryType.LEASING, "leasingEntry");
			// Wizard de Cuotas de leasing.			
			controllerManager.register(AccountEntryType.LEASING_FEE, "leasingFeeEntry");
		}
		return controllerManager;
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
	}
	
    @Override
    public void onSearch(ActionEvent event) {
        super.onSearch(event);
        if (model.getRowCount() > 0) {
            model.setRowIndex(0);
            super.onSelect(null);
        }
    }

    public boolean isRemovable() {
    	if (isNew()) {
    		return false;
    	}
    	if (isManual()) {
    		return true;
    	}
    	AccountEntry entry = (AccountEntry) this.getTo();
    	if (entry == null) {
    		return false;
    	}
    	AccountEntryType type = entry.getType();
        return (type == AccountEntryType.OPENING 
        		|| type == AccountEntryType.OPERATING 
        		|| type == AccountEntryType.CLOSING);
    	
    }
    
/*
    @Override
    public void onRemove(ActionEvent event) {
        super.onRemove(event);
        try {
            if (this.getModel().getRowCount() > 0) {
                this.setTo((AccountEntry)this.getModel().getRowData());
            }
        } catch (ManagerBeanException e) {
			String msg = "No se pudo borrar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
        }
    }
*/
	public void onSelectEntry(ActionEvent event) throws ManagerBeanException {
		AccountEntry entry = (AccountEntry)this.getModel().getRowData();
		this.setTo(entry);
		
		ISpecialAccountEntry c = getControllerManager().getSpecialEntryController(entry.getType());
		c.loadEntry(entry);
		
	}
	
	public String searchAction() {
		try {
			return (getModel().getRowCount() > 0 )?"accountEntry_form":"accountEntry_list";
		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar la búsqueda.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public String onNavigate() {
		AccountEntry entry = (AccountEntry)getTo(); 
		
		ISpecialAccountEntry c = getControllerManager().getSpecialEntryController(entry.getType());
		return c.getNavigationKey();
	}
	
	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null ){
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			String field = event.getComponent().getId();
			getCriteria().addEqualExpression(accountEntryBean.getFieldName(field), event.getNewValue());
		}
	}

    public boolean isManual() {
    	AccountEntry entry = (AccountEntry) this.getTo();
        return (this.getTo() != null && (entry.getType() == AccountEntryType.MANUAL));
    }

    @SuppressWarnings("unchecked")
    public double getTotalDebit() {
        double debit = 0;
        if (this.getTo() != null ) {
	        try {
	            Integer id = ((AccountEntry)this.getTo()).getId();
	            IManagerBean detailsBean = BeanManager.getManagerBean(AccountEntryDetail.class);
	            Criteria criteria = new Criteria();
	            criteria.addEqualExpression(detailsBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
	            Iterator iterator = detailsBean.getList(criteria).iterator();
	            while (iterator.hasNext()) {
	                AccountEntryDetail detail = (AccountEntryDetail)iterator.next();
	                debit += detail.getDebit();
	            }
	        } catch (ManagerBeanException e) {
	            LOGGER.log(Level.SEVERE, "Error getting Account Entry Details", e);
	        }
        }
        return debit;
    }

    @SuppressWarnings("unchecked")
    public double getTotalCredit() {
        double credit = 0;
        if (this.getTo() != null ) {
	        try {
	            Integer id = ((AccountEntry)this.getTo()).getId();
	            IManagerBean detailsBean = BeanManager.getManagerBean(AccountEntryDetail.class);
	            Criteria criteria = new Criteria();
	            criteria.addEqualExpression(detailsBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
	            Iterator iterator = detailsBean.getList(criteria).iterator();
	            while (iterator.hasNext()) {
	                AccountEntryDetail detail = (AccountEntryDetail)iterator.next();
	                credit += detail.getCredit();
	            }
	        } catch (ManagerBeanException e) {
	            LOGGER.log(Level.SEVERE, "Error getting Account Entry Details", e);
	        }
        }
        return credit;
    }
}
