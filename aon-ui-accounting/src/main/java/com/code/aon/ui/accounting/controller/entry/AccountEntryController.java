package com.code.aon.ui.accounting.controller.entry;

import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountEntryController.class.getName());
	
	private SpecialEntryControllerManager controllerManager;
	
	private boolean periodActive;
	private boolean updatable;
	private boolean updatableViaWizard;
	private boolean aonInvoice;
	
	private Date duplicateDate;
	private String duplicateConcept;
	
	private Double totalDebit;
	private Double totalCredit;

	private boolean commentPanelVisible;
	private boolean duplicateEntryPanelVisible;
	
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
			// Wizard de Cuotas de préstamos.	
			//¿??¿¿?¿?¿?¿¿??¿
			controllerManager.register(AccountEntryType.LOAN_FEE, "loanFeeEntry");
		}
		return controllerManager;
	}
	
	public boolean isPeriodActive() {
		return periodActive;
	}
	public void setPeriodActive(boolean periodActive) {
		this.periodActive = periodActive;
	}

	public boolean isUpdatable() {
		return updatable;
	}
	public void setUpdatable(boolean updatable) {
		this.updatable = updatable;
	}
    public boolean isUpdatableViaWizard() {
		return updatableViaWizard;
	}
	public void setUpdatableViaWizard(boolean updatableViaWizard) {
		this.updatableViaWizard = updatableViaWizard;
	}
	
	public boolean isAonInvoice() {
		return aonInvoice;
	}
	public void setAonInvoice(boolean aonInvoice) {
		this.aonInvoice = aonInvoice;
	}
	
	public void calculateUpdatableFlag() {
		AccountEntry entry = (AccountEntry) this.getTo();
		AccountEntryType type = entry.getType();
		setAonInvoice(false);
		setPeriodActive(true);
		boolean flag = false;
		try {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Period period = (Period) periodBean.get(entry.getAccountPeriod());
			if (period == null) {
				String msg = "No existe el ejercicio contable " + entry.getAccountPeriod();
	            LOGGER.error(msg);
	            AonUtil.addErrorMessage(msg);
	            throw new AbortProcessingException(msg);
			}
			AccountPeriodStatus st = period.getStatus(); 
            if (st == AccountPeriodStatus.INACTIVE || st == AccountPeriodStatus.CLOSED) {
            	setPeriodActive(false);
            } else {
    			flag = isManual();
    			if (!flag && isInvoice()) {
    				flag = isAccountInvoice(entry);
    			}
            }
		} catch (ManagerBeanException e) {
			String msg = "Error al identificar la posibilidad de modificar el apunte";
            LOGGER.error(msg);
            AonUtil.addErrorMessage(msg);
            flag = false;
		}
		setUpdatable(isPeriodActive() && flag);
		setUpdatableViaWizard(isPeriodActive() && (type == AccountEntryType.COLLECTION || type == AccountEntryType.PAYMENT || isAccountInvoice()));
	}
	
	private boolean isAccountInvoice() {
		return (isInvoice() && !isAonInvoice());
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
//			setDocumentNumber( aei.getInvoice().getDocumentNumber() );
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
        return (this.getTo() != null && 
        		(
        		(entry.getType() == AccountEntryType.MANUAL)
        		|| (entry.getType() == AccountEntryType.EXPENSES)
        		|| (entry.getType() == AccountEntryType.SALARY)
        		|| (entry.getType() == AccountEntryType.SOCIAL_INSURANCE)
        		|| (entry.getType() == AccountEntryType.SOCIAL_INSURANCE_ADJUST)
        		|| (entry.getType() == AccountEntryType.LOAN)
        		|| (entry.getType() == AccountEntryType.LOAN_FEE)
        		)
        		);
    }

    public boolean isRemovable() {
    	if (isNew()) {
    		return false;
    	}
    	if (isManual() && isPeriodActive()) {
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
		if (isNew()) {
			super.onCancel(event);	
		}
		super.onEditSearch(event);
		setBackAction(null);
	}
	
    @Override
    public void onSearch(ActionEvent event) {
        super.onSearch(event);
        if (model.getRowCount() > 0) {
            model.setRowIndex(0);
            super.onSelect(null);
        } else {
			AonUtil.addInfoMessage( AonUtil.getMessage("aon_search_no_results") );
			onEditSearch(event);
        }
    }

    @Override
	public void onRemove(ActionEvent event) {
		remove(event);
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
    
    public void onChangeSecurityLevel(ActionEvent event)  {
   		AccountEntry entry = (AccountEntry) getTo();
		entry.setSecurityLevel(entry.getSecurityLevel()==SecurityLevel.OFFICIAL?SecurityLevel.CONFIDENTIAL:SecurityLevel.OFFICIAL);
		accept(event);
    }
    
	public String backAction() {
		String b = super.backAction();
		setBackAction(null);
		return b;
	}
	
	public boolean isBackActionEnabled() {
		return !(IAccountingConstants.ACCOUNT_ENTRY_LIST_NAVKEY.equals(super.backAction()));
	}
	public boolean isStatementAvailable() {
		// Si se ha accedido al manto. de apuntes desde el extracto, se deshabilita 
		// la opción de ir al extracto desde las líneas de apuntes, porque se  
		// cambiaría el contenido del controlador del extracto. 
		return !(IAccountingConstants.ACCOUNT_STMT_LIST_NAVKEY.equals(super.backAction()) );
	}

	public String searchAction() {
		try {
			return (getModel().getRowCount() > 0 )?IAccountingConstants.ACCOUNT_ENTRY_FORM_NAVKEY:IAccountingConstants.ACCOUNT_ENTRY_SEARCH_NAVKEY;
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
				List<?> dets = detailsBean.getList(pl, criteria);
	        	setTotalDebit(IAccountingConstants.ZERO);
	        	setTotalCredit(IAccountingConstants.ZERO);
				if (dets.size() > 0) {
					if (dets.get(0) != null) {
						Object[] values = (Object[]) dets.get(0);
						setTotalDebit(values[0] != null ? (Double) values[0]: IAccountingConstants.ZERO );	
						setTotalCredit(values[1] != null ? (Double) values[1]: IAccountingConstants.ZERO );
					} 
				}
	        } catch (ManagerBeanException e) {
	        	setTotalDebit(IAccountingConstants.ZERO);
	        	setTotalCredit(IAccountingConstants.ZERO);
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
	
	public boolean isCommentPanelVisible() {
		return commentPanelVisible;
	}
	public void setCommentPanelVisible(boolean commentPanelVisible) {
		this.commentPanelVisible = commentPanelVisible;
	}
	public boolean isDuplicateEntryPanelVisible() {
		return duplicateEntryPanelVisible;
	}
	public void setDuplicateEntryPanelVisible(boolean duplicateEntryPanelVisible) {
		this.duplicateEntryPanelVisible = duplicateEntryPanelVisible;
	}
	
  	public void showCommentPanel(ActionEvent event  ) {
		setCommentPanelVisible(true);
	}
	public void hideCommentPanel(ActionEvent event  ) {
		setCommentPanelVisible(false);
	}
	
  	public void showDuplicateEntryPanel(ActionEvent event  ) {
		try {
	  		AccountEntry entry = (AccountEntry) getTo();
	  		setDuplicateDate(entry.getEntryDate());
	  		IController detail = FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_DETAIL_NAME);
	  		List<?> list = (List<?>) detail.getModel().getWrappedData();
	  		if (list == null || list.size() == 0) {
				String msg = "No hay líneas en el apunte. No se puede duplicar";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
	  		}
	  		setDuplicateConcept(((AccountEntryDetail) list.get(0)).getConcept());
			setDuplicateEntryPanelVisible(true);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo duplicar el apunte. [" + e.getLocalizedMessage()+ "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	public void hideDuplicateEntryPanel(ActionEvent event  ) {
		setDuplicateEntryPanelVisible(false);
	}
	
	
	public Date getDuplicateDate() {
		return duplicateDate;
	}
	public void setDuplicateDate(Date duplicateDate) {
		this.duplicateDate = duplicateDate;
	}

	public String getDuplicateConcept() {
		return duplicateConcept;
	}
	public void setDuplicateConcept(String duplicateConcept) {
		this.duplicateConcept = duplicateConcept;
	}

	public String onLoadInvoice() throws ManagerBeanException {
		String invoiceViewer = null;
   		AccountEntry entry = (AccountEntry) getTo();
		IManagerBean aeiBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(aeiBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID),entry.getId());
		List<ITransferObject> list = aeiBean.getList(criteria);
		if (list != null && list.size() > 0 ) {
			Invoice invoice = ((AccountEntryInvoice)list.get(0)).getInvoice();	
			String invoiceControllerName = "";
			if (invoice.getType() == InvoiceType.SALES) {
				invoiceControllerName = IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.SALE_INVOICE_FORM_NAME;
			} else if (invoice.getType() == InvoiceType.PURCHASE) {
				invoiceControllerName = IFinanceConstants.PURCHASE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.PURCHASE_INVOICE_FORM_NAME;
			} else if (invoice.getType() == InvoiceType.EXPENSES) {
				invoiceControllerName = IFinanceConstants.EXPENSE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.EXPENSE_INVOICE_FORM_NAME;
			} else if (invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
				invoiceControllerName = IFinanceConstants.UNDEDUCTIBLE_INVOICE_CONTROLLER_NAME;
				invoiceViewer = IFinanceConstants.UNDEDUCTIBLE_INVOICE_FORM_NAME;
			}
			InvoiceController invoiceController = (InvoiceController) AonUtil.getRegisteredBean(invoiceControllerName);
			invoiceController.onLoadInvoice(null, invoice, IAccountingConstants.ACCOUNT_ENTRY_FORM_NAVKEY, invoiceControllerName);
		}
		return invoiceViewer;
	}
	
	public void onDuplicate(ActionEvent event) {
    	try {
    		AccountEntry entry = (AccountEntry) this.getModel().getRowData();
    		AccountEntry dup = new AccountEntry();
    		
    		dup.setAccountPeriod(entry.getAccountPeriod());
    		dup.setEntryDate(getDuplicateDate());
    		dup.setType(entry.getType());
    		dup.setSecurityLevel(entry.getSecurityLevel());
    		dup = (AccountEntry) getManagerBean().insert(dup);
    		
    		IManagerBean linesBean = BeanManager.getManagerBean(AccountEntryDetail.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(linesBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
    		List<ITransferObject> list = linesBean.getList(criteria);
    		for (ITransferObject to: list) {
    			AccountEntryDetail detail = (AccountEntryDetail) to;
    			AccountEntryDetail dupDetail = new AccountEntryDetail();
    			
    			dupDetail.setAccountEntry(dup);
    			dupDetail.setLine(detail.getLine());
    			dupDetail.setAccount(detail.getAccount());
    			dupDetail.setConcept(getDuplicateConcept());
    			dupDetail.setBalancingAccount(detail.getBalancingAccount());
    			dupDetail.setDebit(detail.getDebit());
    			dupDetail.setCredit(detail.getCredit());
    			dupDetail.setDocumentNumber(detail.getDocumentNumber());
    			
    			linesBean.insert(dupDetail);
    		}

			criteria = new Criteria();
			criteria.addEqualExpression(getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), dup.getId());
			setCriteria(criteria);
			onSearch(null);
			getModel().setRowIndex(0);
			onSelect(null);

    	} catch (ManagerBeanException e) {
    		String msg = "Imposible duplicar el apunte. " +e.getMessage(); 
    		AonUtil.addErrorMessage( msg );
    		throw new AbortProcessingException(msg,e);
    	}
	}
}
