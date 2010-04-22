package com.code.aon.ui.accounting.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountEntryController.class.getName());
	private static final Double ZERO = new Double(0);
	
	private SpecialEntryControllerManager controllerManager;
	private String backAction;
	
	private boolean updatable;
	private boolean aonInvoice;
	private String documentNumber;
	
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
			// Wizard de Ajustes Seguridad Social.
			controllerManager.register(AccountEntryType.SOCIAL_INSURANCE_ADJUST, "socialInsuranceEntry");
			// Wizard de Creación de préstamos.
			controllerManager.register(AccountEntryType.LOAN, "loanEntry");
			// Wizard de Cuotas de préstamos.			
			controllerManager.register(AccountEntryType.LOAN_FEE, "loanFeeEntry");
		}
		return controllerManager;
	}
	
	public boolean isUpdatable() {
		return updatable;
	}
	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}
	public boolean isAonInvoice() {
		return aonInvoice;
	}
	public void setAonInvoice(boolean aonInvoice) {
		this.aonInvoice = aonInvoice;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	
	public void calculateUpdatableFlag() {
		setAonInvoice(false);
		setDocumentNumber(null);
		boolean flag = false;
		try {
			AccountEntry entry = (AccountEntry) this.getTo();
			flag = (this.getTo() != null && (entry.getType() == AccountEntryType.MANUAL));
			if (!flag && isInvoice()) {
				flag = !isAccountInvoice(entry);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al identificar la posibilidad de modificar el apunte";
            LOGGER.error(msg);
            AonUtil.addErrorMessage(msg);
            flag = false;
		}
		setUpdatable(flag);
	}
	
	public boolean isInvoice() {
		AccountEntry entry = (AccountEntry) this.getTo();
		return (entry != null && (entry.getType() == AccountEntryType.SALES_INVOICE
				|| entry.getType() == AccountEntryType.PURCHASE_INVOICE
				|| entry.getType() == AccountEntryType.EXPENSE_INVOICE
				|| entry.getType() == AccountEntryType.INVESTMENT_INVOICE));
	}
	
	private boolean isAccountInvoice(AccountEntry entry) throws ManagerBeanException {
		IManagerBean aeiBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(aeiBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID),entry.getId());
		List<ITransferObject> list = aeiBean.getList(criteria);
		if (list.size() > 0  ) {
			AccountEntryInvoice aei = (AccountEntryInvoice) list.get(0);
			IManagerBean idBean = BeanManager.getManagerBean(InvoiceDetail.class);
			setDocumentNumber( aei.getInvoice().getDocumentNumber() );
			criteria = new Criteria();
			criteria.addEqualExpression(idBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), aei.getInvoice().getId());
			List<ITransferObject> details = idBean.getList(criteria);
			for (ITransferObject to: details) {
				InvoiceDetail id = (InvoiceDetail) to;
				if (id.getSource() == InvoiceSource.ACCOUNT) {
					return true;					
				}
			}
			setAonInvoice( true );
			return false;
		}
		return true;
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

    public void onSelectEntry(ActionEvent event)  {
    	try {
    		AccountEntry entry = (AccountEntry)this.getModel().getRowData();
    		this.setTo(entry);
		
    		ISpecialAccountEntry c = getControllerManager().getSpecialEntryController(entry.getType());
    		c.loadEntry(entry);
    	} catch (ManagerBeanException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e.getMessage(),e);
    	}
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
	            LOGGER.error("Error getting Account Entry Details", e);
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
