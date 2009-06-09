package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Leasing;
import com.code.aon.accounting.LeasingFeeEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountUtils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.utils.AccountPeriodValidator;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class LeasingFeeEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = Logger.getLogger(LeasingFeeEntryController.class.getName());
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private AccountEntryInvoice accountEntryInvoice;
	
	private boolean isNew;
	
	private LeasingFeeEntryHeader header;

	private AccountUtils accountUtils;

	public AccountUtils getAccountUtils() {
		if (accountUtils == null) {
			accountUtils = new AccountUtils();
		}
		return accountUtils;
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

	public LeasingFeeEntryHeader getHeader() {
		return header;
	}

	public void setHeader(LeasingFeeEntryHeader header) {
		this.header = header;
	}

	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.header = initializeHeader();
	}

	private LeasingFeeEntryHeader initializeHeader() {
		LeasingFeeEntryHeader header = new LeasingFeeEntryHeader();
		header.setLeasingFeeDate(new Date());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		header.setLeasing(new Leasing());
		return header;
	}
	
	public void accept(ActionEvent event) throws ManagerBeanException{
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
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
				entry.setType(AccountEntryType.LEASING_FEE);
				Invoice invoice = insertInvoice();
				insertInvoiceDetail(invoice);
				entry = insertorUpdateAccountEntry(entry);
				insertEntryDetails(entry, invoice);
				this.setAccountEntryInvoice(insertAccountEntryInvoice(entry, invoice));
				this.isNew = false;
				loadAccountEntryController(entry);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
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
			Account rBankAccount = AccountUtil.obtainRBankAccount(getHeader().getRBank());
			Account leasingAccount = AccountUtil.obtainLeasingAccount(getHeader().getLeasing());
			detail.setAccount(rBankAccount);
			detail.setAccountEntry(entry);
			StringBuilder builder = new StringBuilder();
			if (header.getConcept() != null) {
				builder.append(header.getConcept().getDescription());
				builder.append(" ");
			}
			builder.append(invoice.getSeries());
			builder.append("/");
			builder.append(invoice.getNumber());
			detail.setConcept(builder.toString());
			detail.setCredit(getHeader().getTotal());
			detail.setBalancingAccount(leasingAccount);
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(leasingAccount);
			detail.setAccountEntry(entry);
			detail.setConcept(builder.toString());
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
			detail.setConcept(builder.toString());
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
	
	public void onRemove(ActionEvent event){
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				deleteAccountEntryInvoice(this.getAccountEntryInvoice());
				deleteInvoice(getAccountEntryInvoice().getInvoice());
				deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
				deleteAccountEntry(getAccountEntryInvoice().getAccountEntry());
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
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
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
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
			AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountEntryController", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID), entry.getId());
		Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
		if(iter.hasNext()){
			AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iter.next();
			setAccountEntryInvoice(accountEntryInvoice);
			LeasingFeeEntryHeader header = new LeasingFeeEntryHeader();
			AccountEntryDetail detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
			header.setRBank(AccountUtil.obtainRBank(detail.getAccount().getId()));
			detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT).getId() + "");
			header.setInterest(detail.getDebit());
			detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountConstants.LEASING_ACCOUNT_PREFIX + "*");
			header.setAmortization(detail.getDebit());
			detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.FINANCIAL_EXPENSES_ACCOUNT).getId() + "");
			header.setExpenses(detail.getDebit());
			header.setLeasing(obtainLeasing(entry));
			header.setLeasingFeeDate(accountEntryInvoice.getInvoice().getIssueDate());
			header.setSecurityLevel(accountEntryInvoice.getInvoice().getSecurityLevel());
			header.setSeries(accountEntryInvoice.getInvoice().getSeries());
			header.setNumber(accountEntryInvoice.getInvoice().getNumber());
			header.setReferenceCode(accountEntryInvoice.getInvoice().getReferenceCode());
			setHeader(header);
		}
	}

	@SuppressWarnings("unchecked")
	private Leasing obtainLeasing(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountConstants.LEASING_ACCOUNT_PREFIX + "*");
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LeasingAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LEASING_ACCOUNT_ACCOUNT_ID), detail.getAccount().getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LeasingAccount)iter.next()).getLeasing();
		}
		return null;
	}

	@Override
	public String getNavigationKey() {
		return "account_leasing_fee_entry";
	}
	
	public String getPeriodMessage() {
		try {
			return AccountPeriodValidator.getValidAccountPeriod(getHeader().getLeasingFeeDate());
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			return " - ";
		}
	}
}