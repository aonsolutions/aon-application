package com.code.aon.ui.account.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.AccountInvoiceDetail;
import com.code.aon.account.AccountLeasingFeeHeader;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.Leasing;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Tax;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class AccountLeasingFeeController {

	private static final Logger LOGGER = Logger.getLogger(AccountLeasingFeeController.class.getName());
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private AccountEntryInvoice accountEntryInvoice;
	
	private boolean isNew;
	
	private boolean isNewDetail;
	
	private AccountLeasingFeeHeader header;

	private DataModel details;
	
	private AccountInvoiceDetail currentDetail;
	

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

	public AccountLeasingFeeHeader getHeader() {
		return header;
	}

	public void setHeader(AccountLeasingFeeHeader header) {
		this.header = header;
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
	}

	private AccountLeasingFeeHeader initializeHeader() {
		AccountLeasingFeeHeader header = new AccountLeasingFeeHeader();
		header.setLeasingFeeDate(new Date());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		Leasing leasing = new Leasing();
		leasing.setRegistryBank(new RegistryBank());
		header.setLeasing(leasing);
		header.setRegistryBank(new RegistryBank());
		return header;
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
	
	@SuppressWarnings("unused")
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
	
	public void onTaxChange(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxBean.getFieldName(IProductAlias.TAX_ID), event.getNewValue());
			Iterator iter = taxBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Tax tax = (Tax)iter.next();
				this.currentDetail.setVat(tax);
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
		entry.setAccountPeriod(AccountUtil.obtainPeriod(getHeader().getLeasingFeeDate()).getId());
		entry.setEntryDate(getHeader().getLeasingFeeDate());
		entry.setJournal(null);
		entry.setSecurityLevel(getHeader().getSecurityLevel());
		Account account = new Account();
		account = obtainLeasingAccount(getHeader().getLeasing());
		entry.setType(AccountEntryType.LEASING_FEE);
		Invoice invoice = insertInvoice();
		insertInvoiceDetails(invoice);
		entry = insertorUpdateAccountEntry(entry);
		insertEntryDetails(entry, invoice);
		this.setAccountEntryInvoice(insertAccountEntryInvoice(entry, invoice));
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	/* NO se llama a AccountUtil porque este aquí no se genera si no existe */
	private Account obtainLeasingAccount(Leasing leasing) throws ManagerBeanException {
		IManagerBean leasingAccountBean = BeanManager.getManagerBean(LeasingAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(leasingAccountBean.getFieldName(IAccountBridgeAlias.LEASING_ACCOUNT_LEASING_ID), leasing.getId());
		Iterator iter = leasingAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LeasingAccount)iter.next()).getAccount();
		}
		return null;
	}
	
	private AccountEntry insertorUpdateAccountEntry(AccountEntry entry) {
		try {
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			if(this.isNew){
				entry = (AccountEntry)entryBean.insert(entry);
			}else{
				entry = (AccountEntry)entryBean.update(entry);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting AccountEntry", e);
		}
		return entry;
	}
	
	private void insertEntryDetails(AccountEntry entry, Invoice invoice) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			Account rBankAccount = AccountUtil.obtainRBankAccount(getHeader().getRegistryBank());
			detail.setAccount(rBankAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept("N/Fra: " + invoice.getSeries() + "/" + invoice.getNumber());
			detail.setCredit(getInvoiceTotal());
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			Account vatAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT);
			detail.setAccount(vatAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept("N/Fra: " + invoice.getSeries() + "/" + invoice.getNumber());
			detail.setDebit(obtainVATandSurchargeQuota());
			accountEntryDetailBean.insert(detail);
			// Tercer Apunte
			detail = new AccountEntryDetail();
			Account leasingAccount = obtainLeasingAccount(getHeader().getLeasing());
			detail.setAccount(leasingAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept("N/Fra: " + invoice.getSeries() + "/" + invoice.getNumber());
			detail.setDebit(obtainTotalTaxableBase());
			accountEntryDetailBean.insert(detail);
			// Cuarto Apunte
			detail = new AccountEntryDetail();
			Account account272 = AccountUtil.obtainAccount("272");
			Account account663 = AccountUtil.obtainAccount("663");
			detail.setAccount(account272);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(account663);
			detail.setConcept("Intereses Leasing");
			detail.setCredit(getHeader().getInterest());
			accountEntryDetailBean.insert(detail);
			// Quinto Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(account663);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(account272);
			detail.setConcept("Intereses Leasing");
			detail.setDebit(getHeader().getInterest());
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting details for AccountEntry with id = " + entry.getId(), e);
		}
	}

	public AccountEntryInvoice insertAccountEntryInvoice(AccountEntry entry, Invoice invoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		AccountEntryInvoice accountEntryInvoice = new AccountEntryInvoice();
		accountEntryInvoice.setInvoice(invoice);
		accountEntryInvoice.setAccountEntry(entry);
		return (AccountEntryInvoice)accountEntryInvoiceBean.insert(accountEntryInvoice);
	}
	
	/**
	 * Gets the invoice total.
	 * 
	 * @return the invoice total
	 */
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
	private double obtainTotalTaxableBase() {
		double total = 0.0;
		Iterator iter = ((List)details.getWrappedData()).iterator();
		while(iter.hasNext()){
			AccountInvoiceDetail detail = (AccountInvoiceDetail)iter.next();
			total += detail.getTaxableBase();
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
			invoice.setIssueDate(getHeader().getLeasingFeeDate());
			invoice.setNumber(calculateNextNumber(getHeader().getSeries()));
			// A INVOICE SE LE METE COMPANY EN REGISTRY
			invoice.setRegistry(obtainCompany());
			invoice.setRegistryDocument(getHeader().getLeasing().getSupplierDocument());
			invoice.setRegistryName(getHeader().getLeasing().getSupplierName());
			invoice.setSeries(getHeader().getSeries());
			invoice.setStatus(InvoiceStatus.SCORED);
			invoice.setType(InvoiceType.LEASING);
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
				invoiceDetail.setTaxableBase(detail.getTaxableBase());
				invoiceDetail = (InvoiceDetail) invoiceDetailBean.insert(invoiceDetail);
				insertInvoiceTaxes(invoiceDetail, detail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoiceDetails for invoice with id= " + invoice.getId(), e);
		}
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
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error insertin invoiceTaxes for InvoiceDetail with id= " + invoiceDetail.getId(), e);
		}
		
	}

	private void deleteInvoice(Invoice invoice) {
		deleteInvoiceDetails(invoice);
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoiceBean.remove(invoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting invoice with id=" + invoice.getId(), e);
		}
	}
	
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
	
	private void deleteAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice) {
		try {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			accountEntryInvoiceBean.remove(accountEntryInvoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error deleting accountEntryInvoice with id= " + accountEntryInvoice.getId(), e);
		}
	}
	
	public void onRBankChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), event.getNewValue());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.getHeader().getLeasing().setRegistryBank((RegistryBank)iter.next());
			}
		}
	}

	private Company obtainCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Iterator iter = companyBean.getList(null).iterator();
		if(iter.hasNext()){
			return (Company)iter.next();
		}
		return null;
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
}