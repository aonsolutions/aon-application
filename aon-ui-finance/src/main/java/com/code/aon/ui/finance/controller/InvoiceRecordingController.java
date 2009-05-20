package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.finance.recording.RecordingParameters;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class InvoiceRecordingController extends BasicController{
	
	private static final Logger LOGGER = Logger.getLogger(InvoiceRecordingController.class.getName());
	
	private RecordingParameters recordingParams;

	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;

	private ArrayList<Invoice> checks = new ArrayList<Invoice>();

	private IPriceStrategy priceStrategy;

	public RecordingParameters getRecordingParams() {
		return recordingParams;
	}

	public void setRecordingParams(RecordingParameters recordingParams) {
		this.recordingParams = recordingParams;
	}

	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if(accountEntryInvoiceWriter == null){
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
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
			Invoice invoice = (Invoice)iter.next();
			if(isRecordable(invoice)){
				if (!checks.contains( invoice )) {
					checks.add( invoice );
				}
			}
		}
	}

	@SuppressWarnings("unused")
	public void checkNone(ActionEvent event) {
		clearCheckedInvoices();
	}

	public boolean getRowChecked() {
		Invoice to = (Invoice) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Invoice to = (Invoice) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Invoice to = (Invoice) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<Invoice> getCheckedInvoices() {
		return checks;
	}

	public void clearCheckedInvoices() {
		checks = new ArrayList<Invoice>();
	}

	public void onEditSearch(MenuEvent event) throws ManagerBeanException {
		this.onEditSearch((ActionEvent)event);
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		initializeSearch();
	}
	
	private void initializeSearch() {
		try {
			((PageDataModel)this.getModel()).resize(0);
			recordingParams = new RecordingParameters();
			recordingParams.setInvoiceType(InvoiceType.SALES);
			clearCheckedInvoices();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error initializing search");
			LOGGER.log(Level.SEVERE, "Error initializing search", e);
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	public boolean isModelToRecordable() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return isRecordable(invoice);
	}
	
	private boolean isRecordable(Invoice invoice) throws ManagerBeanException {
		return InvoiceStatus.PENDING.equals(invoice.getStatus());
	}

	public List<SelectItem> getInvoiceTypes() {
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(InvoiceType.SALES, InvoiceType.SALES.getName(AonUtil.getCurrentLocale()));
		types.add(item);
		item = new SelectItem(InvoiceType.PURCHASE, InvoiceType.PURCHASE.getName(AonUtil.getCurrentLocale()));
		types.add(item);
		return types;
	}

	public boolean isSales(){
		return getRecordingParams().getInvoiceType().equals(InvoiceType.SALES);
	}
	
	@SuppressWarnings("unchecked")
	public void onRecordSelected(ActionEvent event){
		Iterator<Invoice> iter = getCheckedInvoices().iterator();
		while(iter.hasNext()){
			Invoice invoice = iter.next();
			recordInvoice(invoice);
		}
		clearCheckedInvoices();
		this.onSearch(null);
	}
	
	@SuppressWarnings("unchecked")
	private void recordInvoice(Invoice invoice) {
		AccountEntry entry = new AccountEntry();
		try {
			entry.setAccountPeriod(AccountUtil.obtainPeriod(invoice.getIssueDate()).getId());
			entry.setEntryDate(invoice.getIssueDate());
			entry.setJournal(null);
			entry.setType((invoice.getType().equals(InvoiceType.SALES)?AccountEntryType.SALES_INVOICE:AccountEntryType.PURCHASE_INVOICE));
			entry.setSecurityLevel(invoice.getSecurityLevel());
			entry = getAccountEntryInvoiceWriter().insertorUpdateAccountEntry(entry, true);
			List taxBreakDown = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
			getAccountEntryInvoiceWriter().insertEntryDetails(entry, (invoice.getType().equals(InvoiceType.SALES)?AccountUtil.obtainCustomerAccount(invoice.getRegistry()):AccountUtil.obtainSupplierAccount(invoice.getRegistry())), invoice.getSeries(), invoice.getNumber(), getPriceStrategy().getTotalPrice(invoice, invoice), getRetentionTotal(taxBreakDown), getTaxQuota(taxBreakDown), obtainBasesPerAccount(invoice));
			getAccountEntryInvoiceWriter().insertAccountEntryInvoice(entry, invoice);
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoice.setStatus(InvoiceStatus.SCORED);
			if(invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null){
				invoice.setRegistryAddress(null);
			}
			invoiceBean.update(invoice);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private double getTaxQuota(List taxBreakDownList) {
		Iterator iter = taxBreakDownList.iterator();
		double taxQuota = 0;
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.VAT)){
				taxQuota = taxQuota + taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota();
			}
		}
		return taxQuota;
	}

	@SuppressWarnings("unchecked")
	private double getRetentionTotal(List taxBreakDownList) {
		Iterator iter = taxBreakDownList.iterator();
		double retentionQuota = 0;
		while(iter.hasNext()){
			TaxBreakDown taxBreakDown = (TaxBreakDown)iter.next();
			if(taxBreakDown.getTaxType().equals(TaxType.RETENTION)){
				retentionQuota = retentionQuota + taxBreakDown.getTaxQuota();
			}
		}
		return retentionQuota;
	}
	
	@SuppressWarnings("unchecked")
	private Map obtainBasesPerAccount(Invoice invoice) throws ManagerBeanException {
		Map basesPerAccount = new HashMap();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			Account account = null;
			if (invoiceDetail.getItem() != null) {
				Integer productId = invoiceDetail.getItem().getProduct().getId();
				ProductAccountType accountType = (invoice.getType().equals(InvoiceType.SALES))?ProductAccountType.SALES:ProductAccountType.PURCHASE; 
				IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				criteria = new Criteria();
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), productId);
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), accountType);
				Iterator iter = productAccountBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					account = ((ProductAccount)iter.next()).getAccount();
				}
			}
			account = (account==null)?account = obtainDefaultAccount(invoice):account;

			double base = invoiceDetail.getTaxableBase();
			base += (basesPerAccount.containsKey(account))?((Double)basesPerAccount.get(account)).doubleValue():0;
			basesPerAccount.put(account, new Double(base));

			insertInvoiceDetailAccount(invoiceDetail, account);
		}
		return basesPerAccount;
	}

	@SuppressWarnings("unchecked")
	private Account obtainDefaultAccount(Invoice invoice) throws ManagerBeanException {
		String paramName = (invoice.getType().equals(InvoiceType.SALES)?DefaultAccounts.SALES_ACCOUNT:DefaultAccounts.PURCHASE_ACCOUNT);
		IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(appParamsBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), paramName);
		Iterator iter = appParamsBean.getList(criteria).iterator();
		if(iter.hasNext()){
			ApplicationParameter param = (ApplicationParameter)iter.next();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria accountCriteria = new Criteria();
			accountCriteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), param.getValue());
			Iterator accountIter = accountBean.getList(accountCriteria).iterator();
			if(accountIter.hasNext()){
				return (Account)accountIter.next();
			}
		}
		return null;
	}

	private void insertInvoiceDetailAccount(InvoiceDetail invoiceDetail, Account account) throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
		invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
		invoiceDetailAccount.setAccount(account);
		invoiceAccountBean.insert(invoiceDetailAccount);
	}

}