package com.code.aon.ui.accounting.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountEntryController.class.getName());
	private static final Double ZERO = new Double(0);
	
	private SpecialEntryControllerManager controllerManager;
	private String backAction;

	private Double totalDebit;
	private Double totalCredit;
	
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
	
    public boolean isManual() {
    	AccountEntry entry = (AccountEntry) this.getTo();
        return (this.getTo() != null && (entry.getType() == AccountEntryType.MANUAL));
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

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		setBackAction(null);
	}
	
    @Override
    public void onSearch(ActionEvent event) {
        super.onSearch(event);
        if (model.getRowCount() > 0) {
            model.setRowIndex(0);
            super.onSelect(null);
        }
    }

    public void onSelectEntry(ActionEvent event) throws ManagerBeanException {
		AccountEntry entry = (AccountEntry)this.getModel().getRowData();
		this.setTo(entry);
		
		ISpecialAccountEntry c = getControllerManager().getSpecialEntryController(entry.getType());
		c.loadEntry(entry);
		
	}
	
	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public boolean isStatementAvailable() {
		// Si se ha accedido al manto. de apuntes desde el extracto, se desahilita 
		// la opción de ir al extracto desde las líneas de apuntes, porque se  
		// cambiaría el contenido del controlador del extracto. 
		return getBackAction() == null || !("account_statement_list".equals(getBackAction()) );
	}

	public String backAction() {
		String b = getBackAction();
		setBackAction(null);
		return b;
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
/*	
	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null ){
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			String field = event.getComponent().getId();
			getCriteria().addEqualExpression(accountEntryBean.getFieldName(field), event.getNewValue());
		}
	}
*/

    @SuppressWarnings("unchecked")
    public void refreshTotals() {
        if (this.getTo() != null ) {
	        try {
	            Integer id = ((AccountEntry)this.getTo()).getId();
	            IManagerBean detailsBean = BeanManager.getManagerBean(AccountEntryDetail.class);
	            Criteria criteria = new Criteria();
	            criteria.addEqualExpression(detailsBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
				ProjectionList pl = new ProjectionList();
				pl.add(Projection.sum(detailsBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_DEBIT)));
				pl.add(Projection.sum(detailsBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_CREDIT)));
				List dets = detailsBean.getList(pl, criteria);
	        	setTotalDebit(ZERO);
	        	setTotalCredit(ZERO);
				if (dets.size() > 0) {
					if (dets.get(0) != null) {
						Object[] values = (Object[]) dets.get(0);
						setTotalDebit(values[0] != null ? (Double) values[0]: ZERO );	
						setTotalCredit(values[1] != null ? (Double) values[1]: ZERO );
					} 
				}
	        } catch (ManagerBeanException e) {
	        	setTotalDebit(ZERO);
	        	setTotalCredit(ZERO);
	            LOGGER.log(Level.SEVERE, "Error getting Account Entry Details", e);
	        }
        } else {
        	setTotalDebit(null);
        	setTotalCredit(null);
        }
        
    }

    public Double getTotalDebit() {
    	if (totalDebit == null) {
    		refreshTotals();
    	}
		return totalDebit;
	}
    public void setTotalDebit(Double totalDebit) {
		this.totalDebit = totalDebit;
	}

	public Double getTotalCredit() {
    	if (totalCredit == null) {
    		refreshTotals();
    	}
		return totalCredit;
	}
	public void setTotalCredit(Double totalCredit) {
		this.totalCredit = totalCredit;
	}
	
	public boolean isBalanced() {
		return (getTotalDebit() != null && 
				getTotalCredit() != null &&
				CommonUtil.round(getTotalDebit().doubleValue()) == 
				CommonUtil.round(getTotalCredit().doubleValue())
				); 
	}
	
}
