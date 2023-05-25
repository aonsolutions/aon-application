package com.code.aon.ui.accounting.controller.entry;

import static com.code.aon.ui.common.ICommonMessages.SEARCH_NO_RESULTS;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.book.AonReportType;
import com.code.aon.ui.accounting.controller.report.JournalReportController;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.InvoiceController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountEntryController extends BasicController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AccountEntryController.class.getName());
	
	private Account debitAccount;
	private Account creditAccount;
	private Double	amount;
	private String  concept;
	private String  numDocument;
	
	
	private SpecialEntryControllerManager controllerManager;
	
	private boolean periodActive;
	private boolean updatable;
	private boolean updatableViaWizard;
	private boolean aonInvoice;
	
	private Period duplicatePeriod;
	private Date duplicateDate;
	private String duplicateConcept;
	private boolean duplicateInvertible;
	
	private Double totalDebit;
	private Double totalCredit;

	private boolean commentPanelVisible;
	private boolean duplicateEntryPanelVisible;
	
	private boolean showAuditInfoWindow;
	
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
			AccountPeriodStatus st = entry.getAccountPeriod().getStatus(); 
            if (st == AccountPeriodStatus.INACTIVE || st == AccountPeriodStatus.CLOSED || st == AccountPeriodStatus.OPERATING) {
            	setPeriodActive(false);
            } else {
    			flag = isManual();
    			if (!flag && isInvoice()) {
    				//flag = isAccountInvoice(entry);
    				// TODO Cambiar de nombre al siguiente método.
    				isAccountInvoice(entry);
    				flag = true;
    			}
            }
		} catch (ManagerBeanException e) {
			String msg = "Error al identificar la posibilidad de modificar el apunte";
            LOGGER.error(msg);
            AonUtil.addErrorMessage(msg);
            flag = false;
		}
		setUpdatable(isPeriodActive() && flag);
		setUpdatableViaWizard(isPeriodActive() && (type == AccountEntryType.COLLECTION || type == AccountEntryType.PAYMENT || (isAccountInvoice() && StringUtils.indexOf(getBackAction(), "Invoice_form") == -1)));
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
	
	public boolean isInvoiceNavigationEnabled() {
		return isInvoice() && !isAccountInvoice();
	}

	private boolean isAccountInvoice(AccountEntry entry) throws ManagerBeanException {
		IManagerBean aeiBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(aeiBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID),entry.getId());
		List<ITransferObject> list = aeiBean.getList(criteria);
		if (list.size() > 0  ) {
			AccountEntryInvoice aei = (AccountEntryInvoice) list.get(0);
			IManagerBean idBean = BeanManager.getManagerBean(InvoiceDetail.class);
//			setDocumentNumber( aei.getInvoice().getDocumentNumber() );
			criteria = new Criteria();
			criteria.addEqualExpression(idBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), aei.getInvoice().getId());
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
    	if (isNevv()) {
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
		if (isNevv()) {
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
			AonUtil.addInfoMessageFromBundle(SEARCH_NO_RESULTS);
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
	            criteria.addEqualExpression(detailsBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
				ProjectionList pl = new ProjectionList();
				pl.add(Projection.sum(detailsBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_DEBIT)));
				pl.add(Projection.sum(detailsBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_CREDIT)));
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
	  		setDuplicatePeriod(entry.getAccountPeriod());
	  		setDuplicateDate(entry.getEntryDate());
	  		IController detail = FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_DETAIL_NAME);
	  		List<?> list = (List<?>) detail.getModel().getWrappedData();
	  		if (list == null || list.size() == 0) {
				String msg = "No hay líneas en el apunte. No se puede duplicar";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
	  		}
	  		setDuplicateConcept(((AccountEntryDetail) list.get(0)).getConcept());
	  		setDuplicateInvertible(false);
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
	
	
	public Period getDuplicatePeriod() {
		return duplicatePeriod;
	}
	public void setDuplicatePeriod(Period duplicatePeriod) {
		this.duplicatePeriod = duplicatePeriod;
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
	
	public boolean isDuplicateInvertible() {
		return duplicateInvertible;
	}
	public void setDuplicateInvertible(boolean duplicateInvertible) {
		this.duplicateInvertible = duplicateInvertible;
	}

	public String onLoadInvoice() throws ManagerBeanException {
		String invoiceViewer = null;
   		AccountEntry entry = (AccountEntry) getTo();
		IManagerBean aeiBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(aeiBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID),entry.getId());
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
			invoiceController.onLoad(null, invoice.getId(), IAccountingConstants.ACCOUNT_ENTRY_FORM_NAVKEY, null);
		}
		return invoiceViewer;
	}
	
	public void onDuplicate(ActionEvent event) {
    	try {
    		AccountEntry entry = (AccountEntry) this.getModel().getRowData();
    		AccountEntry dup = new AccountEntry();
    		
    		dup.setAccountPeriod(getDuplicatePeriod());
    		dup.setEntryDate(getDuplicateDate());
    		dup.setType(entry.getType());
    		dup.setSecurityLevel(entry.getSecurityLevel());
    		dup = (AccountEntry) getManagerBean().insert(dup);
    		
    		IManagerBean linesBean = BeanManager.getManagerBean(AccountEntryDetail.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(linesBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
    		List<ITransferObject> list = linesBean.getList(criteria);
    		for (ITransferObject to: list) {
    			AccountEntryDetail detail = (AccountEntryDetail) to;
    			AccountEntryDetail dupDetail = new AccountEntryDetail();
    			
    			dupDetail.setAccountEntry(dup);
    			dupDetail.setLine(detail.getLine());
    			dupDetail.setAccount(detail.getAccount());
    			dupDetail.setConcept(getDuplicateConcept());
    			dupDetail.setBalancingAccount(detail.getBalancingAccount());
    			dupDetail.setDebit(isDuplicateInvertible()?detail.getCredit():detail.getDebit());
    			dupDetail.setCredit(isDuplicateInvertible()?detail.getDebit():detail.getCredit());
    			dupDetail.setDocumentNumber(detail.getDocumentNumber());
    			
    			linesBean.insert(dupDetail);
    		}

			criteria = new Criteria();
			criteria.addEqualExpression(getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), dup.getId());
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
	public void onExcelJournal(ActionEvent event) {
		ReportManager manager = (ReportManager) AonUtil.getRegisteredBean(IAccountingConstants.REPORT_CONTROLLER);
		manager.setOutputFormat(OutputFormat.XLS);
		onJournal(event,manager,MimeType.MIME_MS_EXCEL,"journalBookXls");
	}
	public void onPDFJournal(ActionEvent event) {
		ReportManager manager = (ReportManager) AonUtil.getRegisteredBean(IAccountingConstants.REPORT_CONTROLLER);
		manager.setOutputFormat(OutputFormat.PDF);
		onJournal(event,manager,MimeType.MIME_PDF,AonReportType.JOURNAL.getReportKey());
	}
	
	private void onJournal(ActionEvent event, ReportManager manager , MimeType mimeType, String reportKey) {
		OutputStream out = null;
		HttpServletResponse response = null;
    	try {
    		AccountEntry entry = (AccountEntry) getTo();
			JournalReportController t = (JournalReportController) AonUtil.getRegisteredBean(IAccountingConstants.JOURNAL_REPORT_CONTROLLER);
			t.onEditSearch(event);
			t.setCoverVisible(false);
			t.setCounterVisible(false);
			t.setPeriod(entry.getAccountPeriod());
			t.setOrder(2);
			t.getCriteria().addEqualExpression(t.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID),entry.getId());
			t.onSearch(event);
			response = DownloadUtil.getResponse();
			out = DownloadUtil.initDownload(response, reportKey, mimeType);
			String outcome = manager.execute(out, reportKey);
    	} catch (ManagerBeanException e) {
    		String msg = "Imposible listar el apunte. " +e.getMessage(); 
    		AonUtil.addErrorMessage( msg );
    		throw new AbortProcessingException(msg,e);
    	} catch (ReportException e) {
    		String msg = "Imposible listar el apunte. " +e.getMessage(); 
    		AonUtil.addErrorMessage( msg );
    		throw new AbortProcessingException(msg,e);
		} catch (IOException e) {
    		String msg = "Imposible listar el apunte. " +e.getMessage(); 
    		AonUtil.addErrorMessage( msg );
    		throw new AbortProcessingException(msg,e);
		} finally {
			DownloadUtil.finishDownload(response, out);
		}
		
	}

	/*
	 * 
	 */
	
	public Account getDebitAccount() {
		return debitAccount;
	}

	public void setDebitAccount(Account debitAccount) {
		this.debitAccount = debitAccount;
	}

	public Account getCreditAccount() {
		return creditAccount;
	}

	public void setCreditAccount(Account creditAccount) {
		this.creditAccount = creditAccount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getNumDocument() {
		return numDocument;
	}

	public void setNumDocument(String numDocument) {
		this.numDocument = numDocument;
	}

	public void accept(ActionEvent event) {
		boolean newRow = isNevv();
		if (newRow) {
			if ((getDebitAccount() != null && getDebitAccount().getId() != null) ||
				(getCreditAccount() != null && getCreditAccount().getId() != null)){
				if (StringUtils.isEmpty(getConcept())) {
					String msg = "No se ha indicado el concepto.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				if (getAmount() == null || getAmount() == 0.0) {
					String msg = "No se ha indicado el importe.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
		}
		super.accept(event);
		if (newRow) {
			IController detailController = FormUtil.getController( IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_DETAIL_NAME);
			detailController.onReset(event);
			if (getDebitAccount() != null && getDebitAccount().getId() != null) {
				AccountEntryDetail detail = (AccountEntryDetail) detailController.getTo();
				detail.setAccount(getDebitAccount());
				detail.setConcept(getConcept());
				detail.setDocumentNumber(getNumDocument());
				detail.setDebit(getAmount());
				if (getCreditAccount() != null && getCreditAccount().getId() != null) {
					detail.setBalancingAccount(getCreditAccount());	
				}
				detailController.onAccept(event);
				detailController.onReset(event);
			}
			if (getCreditAccount() != null && getCreditAccount().getId() != null) {
				AccountEntryDetail detail = (AccountEntryDetail) detailController.getTo();
				detail.setAccount(getCreditAccount());
				detail.setConcept(getConcept());
				detail.setDocumentNumber(getNumDocument());
				detail.setCredit(getAmount());
				if (getDebitAccount() != null && getDebitAccount().getId() != null) {
					detail.setBalancingAccount(getDebitAccount());	
				}
				detailController.onAccept(event);
			}
		}
	}

	@Override
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	@Override
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}

}
