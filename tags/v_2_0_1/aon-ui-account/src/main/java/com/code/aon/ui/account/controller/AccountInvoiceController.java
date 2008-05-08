package com.code.aon.ui.account.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.AccountInvoiceDetail;
import com.code.aon.account.AccountInvoiceHeader;
import com.code.aon.account.Period;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Bank;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Tax;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.Registry;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class AccountInvoiceController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountInvoiceController.class.getName());
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private AccountEntryInvoiceWriter writer;
	
	private AccountEntryInvoice accountEntryInvoice;
	
	private boolean isNew;
	
	private boolean isNewDetail;
	
	private boolean isNewFinance;
	
	private AccountInvoiceHeader header;

	private DataModel details;
	
	private DataModel finances;
	
	private AccountInvoiceDetail currentDetail;
	
	private Finance currentFinance;
	

	public AccountEntryInvoiceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryInvoiceWriter();
		}
		return writer;
	}

	public AccountEntryInvoice getAccountEntryInvoice() {
		return accountEntryInvoice;
	}

	public void setAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice) {
		this.accountEntryInvoice = accountEntryInvoice;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isNewDetail() {
		return isNewDetail;
	}

	public void setNewDetail(boolean isNewDetail) {
		this.isNewDetail = isNewDetail;
	}

	public boolean isNewFinance() {
		return isNewFinance;
	}

	public void setNewFinance(boolean isNewFinance) {
		this.isNewFinance = isNewFinance;
	}

	public AccountInvoiceHeader getHeader() {
		return header;
	}

	public void setHeader(AccountInvoiceHeader header) {
		this.header = header;
	}

	public DataModel getFinances() {
		if(finances == null){
			finances = new ListDataModel(new LinkedList<Finance>());
		}
		return finances;
	}

	public void setFinances(DataModel finances) {
		this.finances = finances;
	}

	public DataModel getDetails() {
		if(details == null){
			details = new ListDataModel(new LinkedList<AccountInvoiceDetail>());
		}
		return details;
	}

	public void setDetails(DataModel details) {
		this.details = details;
	}
	
	public AccountInvoiceDetail getCurrentDetail() {
		return currentDetail;
	}

	public void setCurrentDetail(AccountInvoiceDetail currentDetail) {
		this.currentDetail = currentDetail;
	}

	public Finance getCurrentFinance() {
		return currentFinance;
	}

	public void setCurrentFinance(Finance currentFinance) {
		this.currentFinance = currentFinance;
	}
	
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event){
		reset();
	}
	
	@SuppressWarnings("unused")
	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.header = initializeHeader();
		this.details = new ListDataModel(new LinkedList<AccountInvoiceDetail>());
		this.finances = new ListDataModel(new LinkedList<Finance>());
	}

	private AccountInvoiceHeader initializeHeader() {
		AccountInvoiceHeader header = new AccountInvoiceHeader();
		Account account = new Account();
		account.setEntryEnabled(true);
		header.setAccount(account);
		header.setRegistry(new Registry());
		header.setDate(new Date());
		header.setType(InvoiceType.SALES);
		header.setPeriod(new Period());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		return header;
	}
	
	public boolean isSales(){
		if(this.header != null){
			return header.getType().equals(InvoiceType.SALES);
		}
		return false;
	}
	
	public boolean isPurchase(){
		if(this.header != null){
			return header.getType().equals(InvoiceType.PURCHASE);
		}
		return false;
	}
	
	public boolean isExpense(){
		if(this.header != null){
			return header.getType().equals(InvoiceType.EXPENSES);
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	public void onNewDetail(ActionEvent event){
		this.isNewDetail = true;
		this.currentDetail = new AccountInvoiceDetail();
	}
	
	@SuppressWarnings("unused")
	public void onSelectDetail(ActionEvent event){
		this.currentDetail = (AccountInvoiceDetail)details.getRowData();
	}
	
	@SuppressWarnings({"unchecked", "unused"})
	public void onAddDetail(ActionEvent event){
		((LinkedList)this.details.getWrappedData()).add(this.currentDetail);
		this.currentDetail = new AccountInvoiceDetail();
		this.setNewDetail(false);
	}
	
	@SuppressWarnings({"unused","unchecked"})
	public void onRemoveDetail(ActionEvent event){
		((LinkedList)this.details.getWrappedData()).remove(this.currentDetail);
	}

	@SuppressWarnings("unused")
	public void onCancelDetail(ActionEvent event){
		this.currentDetail = new AccountInvoiceDetail();
		this.setNewDetail(false);
	}
	
	@SuppressWarnings({"unused", "unchecked"})
	public void onUpdateDetail(ActionEvent event){
		int i = ((LinkedList)this.details.getWrappedData()).indexOf(this.currentDetail);
		((LinkedList)this.details.getWrappedData()).remove(i);
		((LinkedList)this.details.getWrappedData()).add(i, this.currentDetail);
		this.currentDetail = new AccountInvoiceDetail();
	}
	
	@SuppressWarnings("unused")
	public void onNewFinance(ActionEvent event){
		this.isNewFinance = true;
		this.currentFinance = initializeFinance();
	}
	
	@SuppressWarnings("unused")
	public void onSelectFinance(ActionEvent event){
		this.currentFinance = (Finance)finances.getRowData();
	}
	
	@SuppressWarnings({"unchecked", "unused"})
	public void onAddFinance(ActionEvent event){
		((LinkedList)this.finances.getWrappedData()).add(this.currentFinance);
		this.currentFinance = initializeFinance();
		this.setNewFinance(false);
	}
	
	@SuppressWarnings({"unchecked", "unused"})
	public void onRemoveFinance(ActionEvent event){
		((LinkedList)this.finances.getWrappedData()).remove(this.currentFinance);
	}

	@SuppressWarnings("unused")
	public void onCancelFinance(ActionEvent event){
		this.currentFinance = initializeFinance();
		this.setNewFinance(false);
	}
	
	@SuppressWarnings({"unused", "unchecked"})
	public void onUpdateFinance(ActionEvent event){
		int i = ((LinkedList)this.finances.getWrappedData()).indexOf(this.currentFinance);
		((LinkedList)this.finances.getWrappedData()).remove(i);
		((LinkedList)this.finances.getWrappedData()).add(i, this.currentFinance);
		this.currentFinance = initializeFinance();
	}

	private Finance initializeFinance() {
		Finance finance = new Finance();
		finance.setBank(new Bank());
		finance.setDueDate(new Date());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setInvoice(new Invoice());
		finance.setPayMethod(new PayMethod());
		finance.setRegistry(new Registry());
		return finance;
	}
	
	@SuppressWarnings("unchecked")
	public void onTaxChange(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxBean.getFieldName(IProductAlias.TAX_ID), event.getNewValue());
			Iterator iter = taxBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Tax tax = (Tax)iter.next();
				if(event.getComponent().getId().startsWith("Account_Vat")){
					this.currentDetail.setVat(tax);
				}else{
					this.currentDetail.setRetention(tax);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onPayMethodChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(payMethodBean.getFieldName(IFinanceAlias.PAY_METHOD_ID), event.getNewValue());
			Iterator iter = payMethodBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.currentFinance.setPayMethod((PayMethod)iter.next());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onRegistryChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registryBean.getFieldName(IRegistryAlias.REGISTRY_ID), event.getNewValue());
			Iterator iter = registryBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.header.setRegistry((Registry)iter.next());
			}
		}
	}
	
	@SuppressWarnings("unused")
	public void accept(ActionEvent event) throws ManagerBeanException{
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryInvoice(this.getAccountEntryInvoice());
			deleteInvoice(getAccountEntryInvoice().getInvoice());
			deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
			entry = this.getAccountEntryInvoice().getAccountEntry();
		}
		entry.setAccountPeriod(getHeader().getPeriod().getId());
		entry.setEntryDate(getHeader().getDate());
		entry.setJournal(null);
		entry.setSecurityLevel(getHeader().getSecurityLevel());
		Account account = new Account();
		if(getHeader().getType().equals(InvoiceType.SALES)){
			entry.setType(AccountEntryType.SALES_INVOICE);
			account = AccountUtil.obtainCustomerAccount(getHeader().getRegistry());
		}else{
			if(getHeader().getType().equals(InvoiceType.PURCHASE)){
				entry.setType(AccountEntryType.PURCHASE_INVOICE);
				account = AccountUtil.obtainSupplierAccount(getHeader().getRegistry());
			}else{
				if(getHeader().getType().equals(InvoiceType.EXPENSES)){
					entry.setType(AccountEntryType.EXPENSE_INVOICE);
					account = AccountUtil.obtainCreditorAccount(getHeader().getRegistry());
				}
			}
		}
		Invoice invoice = insertInvoice();
		insertInvoiceDetails(invoice);
		insertFinances(invoice);
		entry = getWriter().insertorUpdateAccountEntry(entry, this.isNew);
		getWriter().insertEntryDetails(entry, account, header.getAccount(), invoice.getSeries(), invoice.getNumber(), getInvoiceTotal(), obtainTotalRetention(), obtainVATandSurchargeQuota(), obtainTotalTaxableBase());
		this.setAccountEntryInvoice(getWriter().insertAccountEntryInvoice(entry, invoice));
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	/**
	 * Gets the invoice total.
	 * 
	 * @return the invoice total
	 */
	@SuppressWarnings("unchecked")
	private double getInvoiceTotal() {
		double total = 0.0;
		Iterator iter = ((List)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			AccountInvoiceDetail detail = (AccountInvoiceDetail)iter.next();
			total += detail.getTotal();
		}
		return total;
	}
	
	/**
	 * Obtain VA tand surcharge quota.
	 * 
	 * @return the double
	 */
	@SuppressWarnings("unchecked")
	private double obtainVATandSurchargeQuota() {
		double total = 0.0;
		Iterator iter = ((List)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			AccountInvoiceDetail detail = (AccountInvoiceDetail)iter.next();
			total += detail.getVatQuota() + detail.getSurcharge();
		}
		return total;
	}
	
	/**
	 * Obtain total taxable base.
	 * 
	 * @return the double
	 */
	@SuppressWarnings("unchecked")
	private double obtainTotalTaxableBase() {
		double total = 0.0;
		Iterator iter = ((List)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			AccountInvoiceDetail detail = (AccountInvoiceDetail)iter.next();
			total += detail.getTaxableBase();
		}
		return total;
	}
	
	/**
	 * Obtain total retention.
	 * 
	 * @return the double
	 */
	@SuppressWarnings("unchecked")
	private double obtainTotalRetention() {
		double total = 0.0;
		Iterator iter = ((List)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			AccountInvoiceDetail detail = (AccountInvoiceDetail)iter.next();
			total += detail.getRetentionQuota();
		}
		return total;
	}
	
	@SuppressWarnings("unused")
	public void onRemove(ActionEvent event){
		deleteAccountEntryInvoice(this.getAccountEntryInvoice());
		deleteInvoice(getAccountEntryInvoice().getInvoice());
		deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
		deleteAccountEntry(getAccountEntryInvoice().getAccountEntry());
	}
	
	private Invoice insertInvoice() {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			Invoice invoice = new Invoice();
			invoice.setIssueDate(getHeader().getDate());
			if(getHeader().getType().equals(InvoiceType.SALES)){
				invoice.setNumber(calculateNextNumber(getHeader().getSeries()));
			}else{
				invoice.setNumber(getHeader().getNumber());
			}
			invoice.setRegistry(getHeader().getRegistry());
			invoice.setRegistryDocument(getHeader().getDocument());
			invoice.setRegistryName(getHeader().getName());
			invoice.setSeries(getHeader().getSeries());
			invoice.setStatus(InvoiceStatus.SCORED);
			invoice.setType(getHeader().getType());
			invoice.setSecurityLevel(getHeader().getSecurityLevel());
			return (Invoice)invoiceBean.insert(invoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoice", e);
		}
		return null;
	}
	
	private int calculateNextNumber(String series) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), series);
		Projection projection = Projection.max(invoiceBean.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if(value != null){
			return ((Integer)value).intValue() + 1;
		}
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	private void insertInvoiceDetails(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Iterator iter = ((LinkedList)details.getWrappedData()).iterator();
			while(iter.hasNext()){
				AccountInvoiceDetail detail = (AccountInvoiceDetail)iter.next();
				InvoiceDetail invoiceDetail = new InvoiceDetail();
				invoiceDetail.setDeliveryDetail(null);
				invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
				invoiceDetail.setInvoice(invoice);
				invoiceDetail.setItem(null);
				invoiceDetail.setSource(InvoiceSource.ACCOUNT);
				invoiceDetail.setWorkPlace(obtainWorkPlace());
				invoiceDetail.setTaxableBase(detail.getTaxableBase());
				invoiceDetail = (InvoiceDetail) invoiceDetailBean.insert(invoiceDetail);
				insertInvoiceTaxes(invoiceDetail, detail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoiceDetails for invoice with id= " + invoice.getId(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private WorkPlace obtainWorkPlace() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Iterator iter = workPlaceBean.getList(null).iterator();
		if(iter.hasNext()){
			return (WorkPlace)iter.next();
		}
		return null;
	}

	private void insertInvoiceTaxes(InvoiceDetail invoiceDetail, AccountInvoiceDetail detail) {
		try {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			InvoiceTax invoiceTax = new InvoiceTax();
			if(detail.getVat().getPercentage() > 0){
				invoiceTax.setInvoiceDetail(invoiceDetail);
				invoiceTax.setPercentage(detail.getVat().getPercentage());
				invoiceTax.setSurcharge(detail.getVat().getSurcharge());
				invoiceTax.setTaxType(TaxType.VAT);
				invoiceTaxBean.insert(invoiceTax);
			}
			if(detail.getRetention().getPercentage() > 0){
				invoiceTax = new InvoiceTax();
				invoiceTax.setInvoiceDetail(invoiceDetail);
				invoiceTax.setPercentage(detail.getRetention().getPercentage());
				invoiceTax.setSurcharge(detail.getRetention().getSurcharge());
				invoiceTax.setTaxType(TaxType.RETENTION);
				invoiceTaxBean.insert(invoiceTax);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error insertin invoiceTaxes for InvoiceDetail with id= " + invoiceDetail.getId(), e);
		}
		
	}

	@SuppressWarnings("unchecked")
	private void insertFinances(Invoice invoice) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Iterator iter = ((LinkedList)finances.getWrappedData()).iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				finance.setInvoice(invoice);
				finance.setRegistry(invoice.getRegistry());
				finance.setFinanceStatus(FinanceStatus.PENDING);
				financeBean.insert(finance);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting finances for invoice with id= " + invoice.getId(), e);
		}
		
	}
	
	
	private void deleteInvoice(Invoice invoice) {
		deleteFinances(invoice);
		deleteInvoiceDetails(invoice);
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoiceBean.remove(invoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting invoice with id=" + invoice.getId(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deleteAccountEntryDetails(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
			Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				accountEntryDetailBean.remove((AccountEntryDetail)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing details related with accountEntry with id= " + accountEntry.getId(), e);
		}
	}
	
	private void deleteAccountEntry(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			accountEntryBean.remove(accountEntry);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting AccountEntry with id=" + accountEntry.getId(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deleteInvoiceDetails(Invoice invoice) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				Criteria taxCriteria = new Criteria();
				taxCriteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator taxIter = invoiceTaxBean.getList(taxCriteria).iterator();
				while(taxIter.hasNext()){
					invoiceTaxBean.remove((InvoiceTax)taxIter.next());
				}
				invoiceDetailBean.remove(invoiceDetail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error deleting invoiceDetails related with invoice with id= " + invoice.getId(),e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void deleteFinances(Invoice invoice) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
			Iterator iter = financeBean.getList(criteria).iterator();
			while(iter.hasNext()){
				financeBean.remove((Finance)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting finances related with invoice with id= " + invoice.getId(), e);
		}
	}
	
	private void deleteAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice) {
		try {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			accountEntryInvoiceBean.remove(accountEntryInvoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error deleting accountEntryInvoice with id= " + accountEntryInvoice.getId(), e);
		}
	}
	
	private void loadAccountEntryController(AccountEntry entry) {
		try {
			AccountEntryController entryController = (AccountEntryController)AonUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountEntryController", e);
		}
	}
	
	public List<SelectItem> getInvoiceTypes(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(InvoiceType.SALES, InvoiceType.SALES.getName(locale));
		types.add(item);
		item = new SelectItem(InvoiceType.PURCHASE, InvoiceType.PURCHASE.getName(locale));
		types.add(item);
		item = new SelectItem(InvoiceType.EXPENSES, InvoiceType.EXPENSES.getName(locale));
		types.add(item);
		return types;
	}
}