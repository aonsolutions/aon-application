package com.code.aon.ui.account.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
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
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.account.utils.AccountPeriodValidator;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class AccountLeasingFeeController {

	private static final Logger LOGGER = Logger.getLogger(AccountLeasingFeeController.class.getName());
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private AccountEntryInvoice accountEntryInvoice;
	
	private boolean isNew;
	
	private AccountLeasingFeeHeader header;


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

	public AccountLeasingFeeHeader getHeader() {
		return header;
	}

	public void setHeader(AccountLeasingFeeHeader header) {
		this.header = header;
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
	public void accept(ActionEvent event) throws ManagerBeanException{
		AccountPeriodValidator.validateAccountPeriod(getHeader().getLeasingFeeDate());
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
		insertInvoiceDetail(invoice);
		entry = insertorUpdateAccountEntry(entry);
		insertEntryDetails(entry, invoice);
		this.setAccountEntryInvoice(insertAccountEntryInvoice(entry, invoice));
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	private void insertInvoiceDetail(Invoice invoice) throws ManagerBeanException {
		InvoiceDetail detail = new InvoiceDetail();
		detail.setInvoice(invoice);
		detail.setDiscountExpression(new DiscountExpression("0.0"));
		detail.setItem(null);
		detail.setPrice(getHeader().getTotal());
		detail.setQuantity(1.0);
		detail.setSource(InvoiceSource.ACCOUNT);
		detail.setTaxableBase(getHeader().getTaxableBase());
		detail.setTaxes(0.0);
		detail.setWorkPlace(obtainWorkPlace());
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		invoiceDetailBean.insert(detail);
		insertInvoiceTaxes(detail);
	}

	/* NO se llama a AccountUtil porque este aquí no se genera si no existe */
	@SuppressWarnings({"unchecked", "unused"})
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
			Account leasingAccount = AccountUtil.obtainLeasingAccount(getHeader().getLeasing());
			detail.setAccount(rBankAccount);
			detail.setAccountEntry(entry);
			detail.setConcept("N/Fra: " + invoice.getSeries() + "/" + invoice.getNumber());
			detail.setCredit(getHeader().getTotal());
			detail.setBalancingAccount(leasingAccount);
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(leasingAccount);
			detail.setAccountEntry(entry);
			detail.setConcept("N/Fra: " + invoice.getSeries() + "/" + invoice.getNumber());
			detail.setDebit(getHeader().getAmortization());
			detail.setBalancingAccount(rBankAccount);
			accountEntryDetailBean.insert(detail);
			// Tercer Apunte
			detail = new AccountEntryDetail();
			Account debtInterestAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT);
			detail.setAccount(debtInterestAccount);
			detail.setAccountEntry(entry);
			detail.setConcept("Intereses Leasing");
			detail.setDebit(getHeader().getInterest());
			detail.setBalancingAccount(rBankAccount);
			accountEntryDetailBean.insert(detail);
			// Cuarto Apunte
			detail = new AccountEntryDetail();
			Account financialExpensesAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.FINANCIAL_EXPENSES_ACCOUNT);
			detail.setAccount(financialExpensesAccount);
			detail.setAccountEntry(entry);
			detail.setConcept("Gastos Financieros");
			detail.setDebit(getHeader().getExpenses());
			detail.setBalancingAccount(rBankAccount);
			accountEntryDetailBean.insert(detail);
			// Quinto Apunte
			detail = new AccountEntryDetail();
			Account vatAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT);
			detail.setAccount(vatAccount);
			detail.setAccountEntry(entry);
			detail.setConcept("N/Fra: " + invoice.getSeries() + "/" + invoice.getNumber());
			detail.setDebit(getHeader().getVatQuota());
			detail.setBalancingAccount(rBankAccount);
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
			invoice.setSeries(getHeader().getSeries());
			invoice.setNumber(getHeader().getNumber());
			invoice.setReferenceCode(getHeader().getReferenceCode());
			// A INVOICE SE LE METE COMPANY EN REGISTRY
			invoice.setRegistry(obtainCompany());
			invoice.setRegistryDocument(getHeader().getLeasing().getSupplierDocument());
			invoice.setRegistryName(getHeader().getLeasing().getSupplierName());
			invoice.setStatus(InvoiceStatus.SCORED);
			invoice.setType(InvoiceType.LEASING);
			invoice.setSecurityLevel(getHeader().getSecurityLevel());
			return (Invoice)invoiceBean.insert(invoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoice", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private WorkPlace obtainWorkPlace() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Iterator iter = workPlaceBean.getList(null, 0, 1).iterator();
		if(iter.hasNext()){
			return (WorkPlace)iter.next();
		}
		return null;
	}

	private void insertInvoiceTaxes(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			InvoiceTax invoiceTax = new InvoiceTax();
			if(getHeader().getLeasing().getVat().getPercentage() > 0){
				invoiceTax.setInvoiceDetail(invoiceDetail);
				invoiceTax.setPercentage(getHeader().getLeasing().getVat().getPercentage());
				invoiceTax.setSurcharge(getHeader().getLeasing().getVat().getSurcharge());
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
	
	private void deleteAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice) {
		try {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			accountEntryInvoiceBean.remove(accountEntryInvoice);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error deleting accountEntryInvoice with id= " + accountEntryInvoice.getId(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onRBankChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), event.getNewValue());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.getHeader().setRegistryBank((RegistryBank)iter.next());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void onLeasingChange(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean leasingBean = BeanManager.getManagerBean(Leasing.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(leasingBean.getFieldName(IAccountAlias.LEASING_ID), event.getNewValue());
			Iterator iter = leasingBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.getHeader().setLeasing((Leasing)iter.next());
			}
		}
	}

	@SuppressWarnings("unchecked")
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