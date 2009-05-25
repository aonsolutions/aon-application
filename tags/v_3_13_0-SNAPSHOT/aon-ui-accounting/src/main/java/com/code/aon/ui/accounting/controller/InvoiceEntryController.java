package com.code.aon.ui.accounting.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.InvoiceEntryDetail;
import com.code.aon.accounting.InvoiceEntryHeader;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountUtils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class InvoiceEntryController implements ISpecialAccountEntry {

	private static final Logger LOGGER = Logger.getLogger(InvoiceEntryController.class.getName());
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	private static final String ACCOUNT_APP_PARAM_CONTROLLER_NAME = "accAppParams";

	private AccountEntryInvoiceWriter writer;

	private FinanceGenerator financeGenerator;

	private AccountEntryInvoice accountEntryInvoice;

	private boolean isNew;

	private boolean isNewDetail;

	private boolean isNewFinance;

	private InvoiceEntryHeader header;

	private DataModel details;

	private DataModel finances;

	private InvoiceEntryDetail currentDetail;

	private Company company;

	private Finance currentFinance;
	private String onGenerateKey;
	private AccountUtils accountUtils;

	public AccountUtils getAccountUtils() {
		if (accountUtils == null) {
			accountUtils = new AccountUtils();
		}
		return accountUtils;
	}
	
	public AccountEntryInvoiceWriter getWriter() {
		if (writer == null) {
			writer = new AccountEntryInvoiceWriter();
		}
		return writer;
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
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

	public boolean isRegistryFilled() {
		return (getHeader() != null && getHeader().getRegistry() != null && getHeader()
				.getRegistry().getId() != null);
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

	public Company getCompany() {
		try {
			if (company == null) {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
				if (iter.hasNext()) {
					setCompany((Company) iter.next());
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("Error obtaining Company!");
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

/*
	public boolean isAccountSource() throws ManagerBeanException {
		if (isNew || getAccountEntryInvoice() == null) {
			return true;
		}
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean
				.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getAccountEntryInvoice()
				.getInvoice().getId());
		Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			InvoiceDetail detail = (InvoiceDetail) iter.next();
			if (!detail.getSource().equals(InvoiceSource.ACCOUNT)) {
				return false;
			}
		}
		return true;
	}
*/
	public void setNewFinance(boolean isNewFinance) {
		this.isNewFinance = isNewFinance;
	}

	public InvoiceEntryHeader getHeader() {
		return header;
	}

	public void setHeader(InvoiceEntryHeader header) {
		this.header = header;
	}

	public DataModel getFinances() {
		if (finances == null) {
			finances = new ListDataModel(new LinkedList<Finance>());
		}
		return finances;
	}

	public void setFinances(DataModel finances) {
		this.finances = finances;
	}

	public DataModel getDetails() {
		if (details == null) {
			details = new ListDataModel(new LinkedList<InvoiceEntryDetail>());
		}
		return details;
	}

	public void setDetails(DataModel details) {
		this.details = details;
	}

	public InvoiceEntryDetail getCurrentDetail() {
		return currentDetail;
	}

	public void setCurrentDetail(InvoiceEntryDetail currentDetail) {
		this.currentDetail = currentDetail;
	}

	public Finance getCurrentFinance() {
		return currentFinance;
	}

	public void setCurrentFinance(Finance currentFinance) {
		this.currentFinance = currentFinance;
	}

	public void onReset(ActionEvent event) {
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void reset() throws ManagerBeanException {
		this.isNew = true;
		this.header = new InvoiceEntryHeader();
		initializeHeader();
		header.setType(InvoiceType.SALES);

		this.details = new ListDataModel(new LinkedList<InvoiceEntryDetail>());
		this.currentDetail = null;
		this.setNewDetail(false);

		this.finances = new ListDataModel(new LinkedList<Finance>());
		this.currentFinance = null;
		this.setNewFinance(false);
	}

	private void initializeHeader() throws ManagerBeanException {
		Account account = new Account();
		account.setEntryEnabled(true);
		header.setAccount(account);
		header.setRegistry(new Registry());
		header.setDate(new Date());
		header.setTaxDate(new Date());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		header.setTransaction(InvoiceTransactionType.NATIONAL);
		header.setInvestment(false);
		header.setTaxFree(false);
		header.setWithholding(false);
		header.setSurcharge(false);
		AccountAppParamsController c = (AccountAppParamsController) AonUtil
				.getRegisteredBean(ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		ApplicationParameter param = c.getParameter(DefaultAccounts.DEFAULT_INVOICE_SERIES);
		if (param != null) {
			header.setSeries(param.getValue());
		}
	}

	public boolean isSales() {
		if (this.header != null) {
			return header.getType().equals(InvoiceType.SALES);
		}
		return false;
	}

	public boolean isPurchase() {
		if (this.header != null) {
			return header.getType().equals(InvoiceType.PURCHASE);
		}
		return false;
	}

	public boolean isExpense() {
		if (this.header != null) {
			return header.getType().equals(InvoiceType.EXPENSES);
		}
		return false;
	}

	public void onNewDetail(ActionEvent event) {
		this.isNewDetail = true;
		this.currentDetail = new InvoiceEntryDetail();
		Account a = (header.getAccount() != null) ? header.getAccount() : null;
		this.currentDetail.setAccount(a);

		AccountAppParamsController c = (AccountAppParamsController) AonUtil
				.getRegisteredBean(ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		try {
			ApplicationParameter param = c.getParameter(DefaultAccounts.DEFAULT_VAT_PERCENT);
			if (param != null) {
				String value = param.getValue();
				Integer id = Integer.parseInt(value);
				IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
				Tax tax = (Tax) taxBean.get(id);
				if (tax != null) {
					this.currentDetail.setVatPercent(tax.getPercentage());
					if (isWithSurcharge()) {
						this.currentDetail.setSurchargePercent(tax.getSurcharge());
					}
				} else {
					LOGGER
							.warning("NO SE PUEDE ASIGNAR EL PORCENTAJE DE IVA POR DEFECTO. ENCONTRADO ["
									+ value + "]");
				}
			}
		} catch (Exception e) {
			LOGGER.warning("NO SE PUEDE ASIGNAR EL PORCENTAJE DE IVA POR DEFECTO.");
		}

		if (isWithHolding()) {
			try {
				ApplicationParameter param = c
						.getParameter(DefaultAccounts.DEFAULT_RETENTION_PERCENT);
				if (param != null) {
					String value = param.getValue();
					Integer id = Integer.parseInt(value);
					IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
					Tax tax = (Tax) taxBean.get(id);
					if (tax != null) {
						this.currentDetail.setRetentionPercent(tax.getPercentage());
					}
				}
			} catch (Exception e) {
				LOGGER.warning("NO SE PUEDE ASIGNAR EL PORCENTAJE DE RETENCION POR DEFECTO.");
			}
		}
	}

	public void onSelectDetail(ActionEvent event) {
		this.currentDetail = (InvoiceEntryDetail) details.getRowData();
	}

	@SuppressWarnings("unchecked")
	public void onAddDetail(ActionEvent event) {
		if (validateDetail(this.currentDetail)) {
			// applySurcharge();
			((List<InvoiceEntryDetail>) this.details.getWrappedData()).add(this.currentDetail);
			this.currentDetail = new InvoiceEntryDetail();
			this.setNewDetail(false);
			onNewDetail(event);
		}
	}

	@SuppressWarnings("unchecked")
	public void onRemoveDetail(ActionEvent event) {
		((LinkedList<InvoiceEntryDetail>) this.details.getWrappedData()).remove(this.currentDetail);
	}

	public void onCancelDetail(ActionEvent event) {
		this.currentDetail = new InvoiceEntryDetail();
		this.setNewDetail(false);
	}

	@SuppressWarnings("unchecked")
	public void onUpdateDetail(ActionEvent event) {
		if (validateDetail(this.currentDetail)) {
			// applySurcharge();
			int i = ((LinkedList<InvoiceEntryDetail>) this.details.getWrappedData())
					.indexOf(this.currentDetail);
			((LinkedList<InvoiceEntryDetail>) this.details.getWrappedData()).remove(i);
			((LinkedList<InvoiceEntryDetail>) this.details.getWrappedData()).add(i,
					this.currentDetail);
			this.currentDetail = new InvoiceEntryDetail();
		}
	}

	private boolean validateDetail(InvoiceEntryDetail detail) {
		if (detail.getAccount() != null && !detail.getAccount().equals("")
				&& detail.getAccount().getId() != null) {
			try {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID),
						detail.getAccount().getId());
				Iterator<ITransferObject> iterator = accountBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					Account account = (Account) iterator.next();
					if (!account.isEntryEnabled()) {
						AonUtil.addErrorMessage("La Cuenta Contable " + detail.getAccount()
								+ " no permite apuntes.");
						return false;
					}
				} else {
					AonUtil.addErrorMessage("La Cuenta Contable " + detail.getAccount()
							+ " no existe.");
					return false;
				}
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("Error al obtener el pojo de la cuenta con id = "
						+ detail.getAccount());
				return false;
			}
		}
		return true;
	}

	public void onNewFinance(ActionEvent event) {
		try {
			this.isNewFinance = true;
			this.currentFinance = initializeFinance();
			Invoice invoice = isNew() ? new Invoice() : getAccountEntryInvoice().getInvoice();
			invoice = mergeInvoice(invoice);
			this.currentFinance.setInvoice(invoice);
			getFinanceGenerator().initializeFinanceData(this.currentFinance, obtainInitialAmount());
			if (this.currentFinance.getBank() == null) {
				this.currentFinance.setBank(new Bank());
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error al inicializar el vencimiento");
		}
	}

	public void onSelectFinance(ActionEvent event) {
		this.currentFinance = (Finance) finances.getRowData();
	}

	@SuppressWarnings("unchecked")
	public void onAddFinance(ActionEvent event) {
		((List<Finance>) this.finances.getWrappedData()).add(this.currentFinance);
		this.currentFinance = initializeFinance();
		this.setNewFinance(false);
	}

	@SuppressWarnings("unchecked")
	public void onRemoveFinance(ActionEvent event) {
		((List<Finance>) this.finances.getWrappedData()).remove(this.currentFinance);
	}

	public void onCancelFinance(ActionEvent event) {
		this.currentFinance = initializeFinance();
		this.setNewFinance(false);
	}

	@SuppressWarnings("unchecked")
	public void onUpdateFinance(ActionEvent event) {
		int i = ((List<Finance>) this.finances.getWrappedData()).indexOf(this.currentFinance);
		((List<Finance>) this.finances.getWrappedData()).remove(i);
		((List<Finance>) this.finances.getWrappedData()).add(i, this.currentFinance);
		this.currentFinance = initializeFinance();
	}

	private Finance initializeFinance() {
		return new Finance();
	}

	@SuppressWarnings("unchecked")
	private double obtainInitialAmount() {
		Iterator<InvoiceEntryDetail> iter = ((List<InvoiceEntryDetail>) this.details
				.getWrappedData()).iterator();
		double detailSum = 0;
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = iter.next();
			detailSum += detail.getTotal();
		}
		Iterator financeIter = ((LinkedList) this.finances.getWrappedData()).iterator();
		double financeSum = 0;
		while (financeIter.hasNext()) {
			Finance finance = (Finance) financeIter.next();
			financeSum += finance.getAmount();
		}
		return CommonUtil.round(detailSum - financeSum);
	}

	public void onTypeChanged(ActionEvent event) {
		try {
			initializeHeader();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public String generate() {
		return onGenerateKey;
	}

	public void onGenerate(ActionEvent event) {
		double invoiceTotal = getInvoiceTotal();
		double financeTotal = getFinanceTotal();
		if (financeTotal > 0 && invoiceTotal != financeTotal) {
			String msg = AonUtil.addErrorMessageFromBundle("financeBundle",
					"finance_unable_record_inaccuracy_error");
			throw new AbortProcessingException(msg);
		}
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);
			AccountEntry entry = null;
			if (!this.isNew) {
				deleteFinances(getAccountEntryInvoice().getInvoice());
				deleteInvoiceDetails(getAccountEntryInvoice().getInvoice());
				deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
				entry = this.getAccountEntryInvoice().getAccountEntry();
			} else {
				entry = new AccountEntry();
			}
			entry.setAccountPeriod(getHeader().getPeriod().getId());
			entry.setEntryDate(getHeader().getDate());
			entry.setJournal(null);
			entry.setSecurityLevel(getHeader().getSecurityLevel());
			Account account = new Account();
			if (getHeader().getType().equals(InvoiceType.SALES)) {
				entry.setType(AccountEntryType.SALES_INVOICE);
				account = AccountUtil.obtainCustomerAccount(getHeader().getRegistry());
			} else {
				if (getHeader().getType().equals(InvoiceType.PURCHASE)) {
					entry.setType(AccountEntryType.PURCHASE_INVOICE);
					account = AccountUtil.obtainSupplierAccount(getHeader().getRegistry());
				} else {
					if (getHeader().getType().equals(InvoiceType.EXPENSES)) {
						entry.setType(AccountEntryType.EXPENSE_INVOICE);
						account = AccountUtil.obtainCreditorAccount(getHeader().getRegistry());
					}
				}
			}
			Invoice invoice = insertOrUpdateInvoice(sessionName);
			insertInvoiceDetails(invoice);
			insertFinances(invoice);
			if (!isNew) {
				entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
			}
			entry = getWriter().insertOrUpdateAccountEntry(entry);
			if (isNew) {
				this.setAccountEntryInvoice(getWriter().insertAccountEntryInvoice(entry, invoice));
			}
			getWriter().insertEntryDetails(entry, account,
					getWriter().obtainConcept(invoice, invoiceTotal), invoiceTotal,
					obtainRetentionQuotasPerAccount(invoice), obtainTaxQuotasPerAccount(invoice),
					obtainBasesPerAccount(details));
			getHeader().setAccountEntryId(entry.getId());
			this.isNew = false;
			// loadAccountEntryController(entry);
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			onViewAccountEntry(event);
			onGenerateKey = "accountEntry_form";
		} catch (Exception e) {
			onGenerateKey = null;
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.log(Level.SEVERE, msg, e);
			}
			String msg = "No se pudo generar el apunte contable. " + e.getMessage();
			LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}

	}

	/**
	 * Gets the invoice total.
	 * 
	 * @return the invoice total
	 */
	public double getInvoiceTotal() {
		double total = 0.0;
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			total += detail.getTotal();
		}
		return total;
	}

	/**
	 * Gets the invoice total.
	 * 
	 * @return the invoice total
	 */
	public double getFinanceTotal() {
		double total = 0.0;
		Iterator<?> iter = ((List<?>) finances.getWrappedData()).iterator();
		while (iter.hasNext()) {
			Finance finance = (Finance) iter.next();
			total += finance.getAmount();
		}
		return total;
	}

	public boolean isSettled() {
		return (finances.getRowCount() == 0 || getInvoiceTotal() == getFinanceTotal());
	}

	/**
	 * Obtain VA tand surcharge quota.
	 * 
	 * @return the double
	 */
	private Map<Account, Double> obtainTaxQuotasPerAccount(Invoice invoice)
			throws ManagerBeanException {
		Account account;
		if (invoice.getType().equals(InvoiceType.SALES)) {
			account = AccountUtil.obtainDefaultAccount(DefaultAccounts.CHARGE_VAT_ACCOUNT);
		} else {
			account = AccountUtil.obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT);
		}

		double total = 0.0;
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			total += detail.getVatQuota() + detail.getSurcharge();
		}

		Map<Account, Double> taxQuotasPerAccountMap = new HashMap<Account, Double>();
		taxQuotasPerAccountMap.put(account, new Double(total));
		return taxQuotasPerAccountMap;
	}

	/**
	 * Get the bases per account map.
	 * 
	 * @return the double
	 */
	private Map<Account, Double> obtainBasesPerAccount(DataModel details) {
		Map<Account, Double> basesPerAccount = new HashMap<Account, Double>();
		Iterator<?> iterator = ((List<?>) details.getWrappedData()).iterator();
		while (iterator.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iterator.next();
			Account account = detail.getAccount();
			double base = detail.getTaxableBase();
			base += (basesPerAccount.containsKey(account)) ? basesPerAccount.get(account)
					.doubleValue() : 0;
			basesPerAccount.put(account, new Double(base));
		}
		return basesPerAccount;
	}

	/**
	 * Obtain total retention.
	 * 
	 * @return the double
	 */
	private Map<Account, Double> obtainRetentionQuotasPerAccount(Invoice invoice)
			throws ManagerBeanException {
		Account account;
		if (invoice.getType().equals(InvoiceType.SALES)) {
			account = AccountUtil.obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT);
		} else {
			account = AccountUtil.obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT);
		}

		double total = 0.0;
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			total += detail.getRetentionQuota();
		}

		Map<Account, Double> retentionQuotasPerAccountMap = new HashMap<Account, Double>();
		retentionQuotasPerAccountMap.put(account, new Double(total));
		return retentionQuotasPerAccountMap;
	}

	public void onRemove(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			deleteAccountEntryInvoice(this.getAccountEntryInvoice());
			deleteFinances(getAccountEntryInvoice().getInvoice());
			deleteInvoiceDetails(getAccountEntryInvoice().getInvoice());
			deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
			deleteAccountEntry(getAccountEntryInvoice().getAccountEntry());

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.log(Level.SEVERE, msg, e);
			}
			String msg = "No se pudo borrar la factura. " + e.getMessage();
			LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private Invoice insertOrUpdateInvoice(String sessionName) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = isNew() ? new Invoice() : getAccountEntryInvoice().getInvoice();
		invoice = mergeInvoice(invoice);
		if (isNew()) {
			invoice = (Invoice) invoiceBean.insert(invoice);
		} else {
			invoice = (Invoice) HibernateUtil.getSession(sessionName).merge(invoice);
			invoice = (Invoice) invoiceBean.update(invoice);
		}
		// Al convertir el proceso en transaccional, el update
		// de invoice se realiza al momento del session.flush()
		return invoice;
	}

	private Invoice mergeInvoice(Invoice invoice) throws ManagerBeanException {
		invoice.setIssueDate(getHeader().getDate());
		invoice.setTaxDate(getHeader().getTaxDate());
		invoice.setSeries(getHeader().getSeries());
		if (getHeader().getType().equals(InvoiceType.SALES)) {
			if (getHeader().getNumber() == 0) {
				invoice.setNumber(calculateNextNumber(getHeader().getSeries(), getHeader()
						.getType()));
			} else {
				invoice.setNumber(getHeader().getNumber());
			}
			getHeader().setReferenceCode(
					obtainReferenceCode(invoice.getSeries(), invoice.getNumber()));
		}
		invoice.setReferenceCode(getHeader().getReferenceCode());
		invoice.setRegistry(getHeader().getRegistry());
		invoice.setRegistryDocument(getHeader().getDocument());
		invoice.setRegistryName(getHeader().getName());
		invoice.setStatus(InvoiceStatus.SCORED);
		invoice.setType(getHeader().getType());
		invoice.setSecurityLevel(getHeader().getSecurityLevel());
		invoice.setInvestment(getHeader().isInvestment());
		invoice.setTransaction(getHeader().getTransaction());
		invoice.setWithholding(getHeader().isWithholding());
		invoice.setTaxFree(getHeader().isTaxFree());
		invoice.setSurcharge(getHeader().isSurcharge());
		return invoice;
	}

	private String obtainReferenceCode(String series, int number) {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(series)) {
			sb.append(series);
			sb.append("/");
		}
		sb.append(number);

		return sb.toString();
	}

	private int calculateNextNumber(String series, InvoiceType invoiceType)
			throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), series);
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE),
				invoiceType);
		Projection projection = Projection.max(invoiceBean
				.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	private void insertInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setSourceId(null);
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setItem(null);

			StringBuilder sb = new StringBuilder();
			sb.append("Fra. Nº: ");
			sb.append(invoice.getReferenceCode());
			sb.append(" del ");
			sb.append(getDateFormatter().format(invoice.getIssueDate()));
			invoiceDetail.setDescription(sb.toString());

			invoiceDetail.setSource(InvoiceSource.ACCOUNT);
			invoiceDetail.setWorkPlace(obtainWorkPlace());
			invoiceDetail.setTaxableBase(detail.getTaxableBase());
			invoiceDetail.setPrice(detail.getTaxableBase());
			invoiceDetail.setQuantity(1);
			invoiceDetail = (InvoiceDetail) invoiceDetailBean.insert(invoiceDetail);
			insertInvoiceTaxes(invoiceDetail, detail);
			insertInvoiceAccounts(invoiceDetail, detail);
		}
	}

	private DateFormat getDateFormatter() {
		return new SimpleDateFormat("dd/MM/yyyy");
	}

	private WorkPlace obtainWorkPlace() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Iterator<ITransferObject> iter = workPlaceBean.getList(null).iterator();
		if (iter.hasNext()) {
			return (WorkPlace) iter.next();
		}
		return null;
	}

	private void insertInvoiceTaxes(InvoiceDetail invoiceDetail, InvoiceEntryDetail detail)
			throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		InvoiceTax invoiceTax = new InvoiceTax();
		if (detail.getVatPercent() > 0) {
			invoiceTax.setInvoiceDetail(invoiceDetail);
			invoiceTax.setPercentage(detail.getVatPercent());
			invoiceTax.setSurcharge(detail.getSurchargePercent());
			invoiceTax.setTaxType(TaxType.VAT);
			invoiceTaxBean.insert(invoiceTax);
		}
		if (detail.getRetentionPercent() > 0) {
			invoiceTax = new InvoiceTax();
			invoiceTax.setInvoiceDetail(invoiceDetail);
			invoiceTax.setPercentage(detail.getRetentionPercent());
			invoiceTax.setSurcharge(0);
			invoiceTax.setTaxType(TaxType.RETENTION);
			invoiceTaxBean.insert(invoiceTax);
		}
	}

	private void insertInvoiceAccounts(InvoiceDetail invoiceDetail, InvoiceEntryDetail detail)
			throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		if (detail.getAccount() != null && !detail.getAccount().equals("")) {
			Account account = detail.getAccount();

			InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
			invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
			invoiceDetailAccount.setAccount(account);
			invoiceAccountBean.insert(invoiceDetailAccount);
		}
	}

	@SuppressWarnings("unchecked")
	private void insertFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Iterator<Finance> iter = ((List<Finance>) finances.getWrappedData()).iterator();
		while (iter.hasNext()) {
			Finance finance = iter.next();
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			finance.setFinanceStatus(FinanceStatus.PENDING);
			financeBean.insert(finance);
		}
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException {
		List<Finance> financeList = new LinkedList<Finance>();
		Invoice invoice = isNew() ? new Invoice() : getAccountEntryInvoice().getInvoice();
		invoice = mergeInvoice(invoice);
		financeList = getFinanceGenerator()
				.generateFinances(invoice, this.getInvoiceTotal(), false);
		this.finances = new ListDataModel(financeList);
	}

	private void deleteAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry
				.getId());
		Iterator<ITransferObject> iter = accountEntryDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			accountEntryDetailBean.remove(iter.next());
		}
	}

	private void deleteAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
	}

	private void deleteInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean
				.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iter = invoiceDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) iter.next();
			Criteria taxCriteria = new Criteria();
			taxCriteria.addEqualExpression(invoiceTaxBean
					.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail
					.getId());
			Iterator<ITransferObject> taxIter = invoiceTaxBean.getList(taxCriteria).iterator();
			while (taxIter.hasNext()) {
				invoiceTaxBean.remove(taxIter.next());
			}

			Criteria accountCriteria = new Criteria();
			accountCriteria.addEqualExpression(invoiceAccountBean
					.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID),
					invoiceDetail.getId());
			Iterator<ITransferObject> accountIter = invoiceAccountBean.getList(accountCriteria)
					.iterator();
			while (accountIter.hasNext()) {
				invoiceAccountBean.remove(accountIter.next());
			}
			invoiceDetailBean.remove(invoiceDetail);
		}
	}

	private void deleteFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID),
				invoice.getId());
		Iterator<ITransferObject> iter = financeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			financeBean.remove(iter.next());
		}
	}

	private void deleteAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice)
			throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		accountEntryInvoiceBean.remove(accountEntryInvoice);
	}

	public void onViewAccountEntry(ActionEvent event) {
		try {
			AccountEntry entry = getAccountEntryInvoice().getAccountEntry();
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			String m = "Error loading AccountEntryController";
			AonUtil.addErrorMessage(m);
			LOGGER.log(Level.SEVERE, m, e);
		}
	}

	public void registryChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Company company = getCompany();
			Registry registry = null;
			if (isSales()) {
				Customer customer = (Customer) event.getNewValue();
				registry = customer.getRegistry();
				getHeader().setWithholding(company.isWithholding() && customer.isWithholding());
				getHeader().setSurcharge(customer.isSurcharge());
				getHeader().setTaxFree(customer.isTaxFree());
			} else if (isPurchase()) {
				Supplier supplier = (Supplier) event.getNewValue();
				registry = supplier.getRegistry();
				getHeader().setWithholding(supplier.isWithholding());
				getHeader().setSurcharge(company.isSurcharge());
				getHeader().setTaxFree(company.isTaxFree());
			} else if (isExpense()) {
				Creditor creditor = (Creditor) event.getNewValue();
				registry = creditor.getRegistry();
				getHeader().setWithholding(creditor.isWithholding());
				getHeader().setSurcharge(company.isSurcharge());
				getHeader().setTaxFree(company.isTaxFree());
			}
			getHeader().setDocument(registry.getDocument());
			getHeader().setName(registry.getFullName());
		} else {
			getHeader().setDocument(null);
			getHeader().setName(null);
			getHeader().setWithholding(false);
			getHeader().setSurcharge(false);
			getHeader().setTaxFree(false);
		}
	}

	public boolean isWithHolding() {
		return getHeader().isWithholding();
	}

	public boolean isWithSurcharge() {
		return ((isSales() || isPurchase()) && getHeader().isSurcharge());
	}

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			currentFinance.setBank(new Bank());
			currentFinance.setBankAccount(new BankAccount());
		}
	}

	public void onBankChanged(LookupChangeEvent event) {
		currentFinance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Bank bank = (Bank) event.getNewValue();
			currentFinance.getBankAccount().setEntity(bank.getCode());
		}
	}

	public void onRBankChanged(ValueChangeEvent event) {
		currentFinance.setBank(null);
		currentFinance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			RegistryBank rbank = (RegistryBank) event.getNewValue();
			currentFinance.setBank(rbank.getBank());
			currentFinance.setBankAccount(rbank.getBankAccount());
		}
	}

	public List<SelectItem> getBanks() {
		try {
			if (getCurrentFinance() != null && getCurrentFinance().getPayMethod() != null) {
				PayMethod pm = getCurrentFinance().getPayMethod();
				if ((isSales() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT)
						|| (!isSales() && pm.getType() == PayMethodType.BANK_TRANSFER)) {
					return getRegistryBanks(getCurrentFinance().getRegistry());
				}
				return getRegistryBanks(getCompany());
			}
			return new LinkedList<SelectItem>();
		} catch (ManagerBeanException e) {
			String m = "Error obtaining Banks!";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m);
		}

	}

	private List<SelectItem> getRegistryBanks(Registry registry) throws ManagerBeanException {
		LinkedList<SelectItem> rBanks = new LinkedList<SelectItem>();
		if (isSales()) {
		}
		IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rBankBean
				.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), registry.getId());
		Iterator<?> iter = rBankBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			RegistryBank rBank = (RegistryBank) iter.next();
			SelectItem item = new SelectItem(rBank, StringUtils.abbreviate(rBank.getBank()
					.getName(), 30)
					+ " [" + rBank.getBankAccount().toString() + "]");
			rBanks.add(item);
		}
		return rBanks;
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String) event.getNewValue());
		SecurityLevel securityLevel = obtainSeriesSecurityLevel((String) event.getNewValue());
		if (getHeader() != null) {
			getHeader().setNumber(number);
			getHeader().setSecurityLevel(securityLevel);
		}
	}

	public void onDateChanged(ActionEvent event) {
		getHeader().setTaxDate(getHeader().getDate());
	}

	@SuppressWarnings("unchecked")
	private SecurityLevel obtainSeriesSecurityLevel(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), seriesId);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			Series series = (Series) iter.next();
			if (series.getSecurityLevel() != null) {
				return series.getSecurityLevel();
			}
		}
		return null;
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		if (StringUtils.isEmpty(seriesId)) {
			criteria.addNullExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES));
		} else {
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES),
					seriesId);
		}
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE),
				InvoiceType.SALES);
		Projection projection = Projection.max(invoiceBean
				.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean
				.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID),
				entry.getId());
		Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice) iter.next();
			setAccountEntryInvoice(accountEntryInvoice);
			InvoiceEntryHeader header = new InvoiceEntryHeader();
			AccountEntryDetail detail = null;
			if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
				header.setType(InvoiceType.SALES);
				detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, "70*");
				header.setAccount((detail != null) ? detail.getAccount() : null);
			}
			if (entry.getType().equals(AccountEntryType.PURCHASE_INVOICE)) {
				header.setType(InvoiceType.PURCHASE);
				detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, "60*");
				header.setAccount((detail != null) ? detail.getAccount() : null);
			}
			if (entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)) {
				header.setType(InvoiceType.EXPENSES);
				detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, "6*");
				header.setAccount((detail != null) ? detail.getAccount() : null);
			}
			header.setDate(entry.getEntryDate());
			header.setDocument(accountEntryInvoice.getInvoice().getRegistryDocument());
			header.setName(accountEntryInvoice.getInvoice().getRegistryName());
			header.setSeries(accountEntryInvoice.getInvoice().getSeries());
			header.setNumber(accountEntryInvoice.getInvoice().getNumber());
			header.setDate(entry.getEntryDate());
			header.setTaxDate(accountEntryInvoice.getInvoice().getTaxDate());
			header.setReferenceCode(accountEntryInvoice.getInvoice().getReferenceCode());
			header.setPeriod(new Period());
			header.getPeriod().setId(entry.getAccountPeriod());
			header.setSecurityLevel(entry.getSecurityLevel());
			header.setRegistry(accountEntryInvoice.getInvoice().getRegistry());
			header.setWithholding(accountEntryInvoice.getInvoice().isWithholding());
			header.setSurcharge(accountEntryInvoice.getInvoice().isSurcharge());
			header.setTaxFree(accountEntryInvoice.getInvoice().isTaxFree());
			header.setInvestment(accountEntryInvoice.getInvoice().isInvestment());
			header.setTransaction(accountEntryInvoice.getInvoice().getTransaction());
			header.setAccountEntryId(entry.getId());
			setHeader(header);
			setFinances(new ListDataModel(
					obtainFinances(accountEntryInvoice.getInvoice())));
			setDetails(new ListDataModel(
					obtainDetails(accountEntryInvoice.getInvoice())));
		}
	}

	@SuppressWarnings("unchecked")
	private List obtainFinances(Invoice invoice) {
		List<Finance> finances = new LinkedList<Finance>();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
			Iterator iter = financeBean.getList(criteria).iterator();
			while(iter.hasNext()){
				finances.add((Finance)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining finances for invoice with id=" + invoice.getId(), e);
		}
		return finances;
	}
	
	private List<InvoiceEntryDetail> obtainDetails(Invoice invoice) {
		List<InvoiceEntryDetail> details = new LinkedList<InvoiceEntryDetail>();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InvoiceEntryDetail detail = new InvoiceEntryDetail();
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				if (invoiceDetail.getSource() != InvoiceSource.ACCOUNT) {
					String msg = "Asiento generado automáticamente. No se puede modificar.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				detail.setTaxableBase(invoiceDetail.getTaxableBase());

				Criteria taxCriteria = new Criteria();
				taxCriteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<?> taxIter= invoiceTaxBean.getList(taxCriteria).iterator();
				while(taxIter.hasNext()){
					InvoiceTax invoiceTax = (InvoiceTax)taxIter.next();
					if(invoiceTax.getTaxType().equals(TaxType.VAT)){
						detail.setVatPercent(invoiceTax.getPercentage());
						detail.setSurchargePercent(invoiceTax.getSurcharge());
						
					} else if(invoiceTax.getTaxType().equals(TaxType.RETENTION)){
						detail.setRetentionPercent(invoiceTax.getPercentage());
					}
				}

				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(invoiceAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<?> accountIter= invoiceAccountBean.getList(accountCriteria).iterator();
				if(accountIter.hasNext()){
					InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)accountIter.next();
					detail.setAccount(invoiceDetailAccount.getAccount());
				}

				details.add(detail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading details for invoice with id=" + invoice.getId(), e);
		}
		return details;
	}

	@Override
	public String getNavigationKey() {
		return "account_invoice_entry";
	}
}