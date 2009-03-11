package com.code.aon.account.bridge.writer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;

/**
 * The Class AccountEntryInvoiceWriter.
 */
public class AccountEntryInvoiceWriter {

	private Account salesDefaultAccount;
	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";

	public void unrecordAndUpdateInvoice(Invoice invoice) throws ManagerBeanException {
		unrecordInvoice(invoice);
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.PENDING);
		if (invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null) {
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);
	}

	public void unrecordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntryInvoice accEntryInvoice = obtainAccountEntryInvoice(invoice);
		if (accEntryInvoice != null) {
			removeAccountEntryInvoice(accEntryInvoice);
		}
	}

	public void recordAndUpdateInvoice(Invoice invoice, IPriceStrategy priceStrategy) throws ManagerBeanException {
		recordInvoice(invoice,priceStrategy);	
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.SCORED);
		if (invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null) {
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);
	}
	
	public void recordInvoice(Invoice invoice, IPriceStrategy priceStrategy) throws ManagerBeanException {
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(AccountUtil.obtainPeriod(invoice.getIssueDate()).getId());
		entry.setEntryDate(invoice.getIssueDate());
		entry.setJournal(null);
		AccountEntryType accountEntryType = null;
		if (invoice.getType() == InvoiceType.SALES) {
			accountEntryType = AccountEntryType.SALES_INVOICE;
		} else if (invoice.getType() == InvoiceType.PURCHASE) {
			accountEntryType = AccountEntryType.PURCHASE_INVOICE;
		} else {
			throw new ManagerBeanException("Unsupported invoice Type '" + invoice.getType() + "'");
		}
		entry.setType(accountEntryType);
		entry.setSecurityLevel(invoice.getSecurityLevel());
		entry = insertOrUpdateAccountEntry(entry);
		List<TaxBreakDown> taxBreakDown = priceStrategy.getTaxBreakDowns(invoice, invoice);
		Account account = (invoice.getType().equals(InvoiceType.SALES) ? 
				AccountUtil.obtainCustomerAccount(invoice.getRegistry()) : 
				AccountUtil.obtainSupplierAccount(invoice.getRegistry()));
		double total = priceStrategy.getTotalPrice(invoice, invoice);
		double retentitonTotal = getRetentionTotal(taxBreakDown);
		double taxQuota = getTaxQuota(taxBreakDown);
		Map<Account, Double> bases = obtainBasesPerAccount(invoice);
		insertEntryDetails(entry, account, obtainConcept(invoice, total), total, retentitonTotal, taxQuota, bases);
		insertAccountEntryInvoice(entry, invoice);
	}

	public String obtainConcept(String prefix, Invoice invoice) {
		return prefix + " " + invoice.getReferenceCode(); 
	}

	public String obtainConcept(Invoice invoice, double total) {
		String prefix = (invoice.getType().equals(InvoiceType.SALES)) ? N_FRA : S_FRA;
		if (total < 0) {
			prefix += " " + ABONO;
		}
		prefix += ": ";
		return obtainConcept(prefix, invoice);
	}

	private Map<Account, Double> obtainBasesPerAccount(Invoice invoice) throws ManagerBeanException {
		Map<Account, Double> basesPerAccount = new HashMap<Account, Double>();
		Criteria criteria = new Criteria();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		criteria.addEqualExpression(invoiceDetailBean
				.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) iterator.next();
			Account account = null;
			if (invoiceDetail.getItem() != null) {
				Integer productId = invoiceDetail.getItem().getProduct().getId();
				IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				criteria = new Criteria();
				criteria.addEqualExpression(productAccountBean
						.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), productId);
				criteria.addEqualExpression(productAccountBean
						.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE),
						ProductAccountType.SALES);
				Iterator<?> iter = productAccountBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					account = ((ProductAccount) iter.next()).getAccount();
				}
			}
			account = (account == null) ? account = obtainSalesDefaultAccount() : account;
			double base = invoiceDetail.getTaxableBase();
			base += (basesPerAccount.containsKey(account)) ? basesPerAccount.get(account)
					.doubleValue() : 0;
			basesPerAccount.put(account, new Double(base));
			insertInvoiceDetailAccount(invoiceDetail, account);
		}
		return basesPerAccount;
	}

	private void insertInvoiceDetailAccount(InvoiceDetail invoiceDetail, Account account)
			throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
		invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
		invoiceDetailAccount.setAccount(account);
		invoiceAccountBean.insert(invoiceDetailAccount);
	}

	private Account obtainSalesDefaultAccount() throws ManagerBeanException {
		if (salesDefaultAccount == null) {
			IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(appParamsBean
					.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME),
					DefaultAccounts.SALES_ACCOUNT);
			Iterator<?> iter = appParamsBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				ApplicationParameter param = (ApplicationParameter) iter.next();
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(accountBean
						.getFieldName(IAccountAlias.ACCOUNT_ID), param.getValue());
				Iterator<?> accountIter = accountBean.getList(accountCriteria).iterator();
				if (accountIter.hasNext()) {
					salesDefaultAccount = (Account) accountIter.next();
				}
			}
		}
		return salesDefaultAccount;
	}

	private double getRetentionTotal(List<TaxBreakDown> taxBreakDownList) {
		Iterator<TaxBreakDown> iter = taxBreakDownList.iterator();
		double retentionQuota = 0;
		while (iter.hasNext()) {
			TaxBreakDown taxBreakDown = iter.next();
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				retentionQuota = retentionQuota + taxBreakDown.getTaxQuota();
			}
		}
		return retentionQuota;
	}

	private double getTaxQuota(List<TaxBreakDown> taxBreakDownList) {
		Iterator<TaxBreakDown> iter = taxBreakDownList.iterator();
		double taxQuota = 0;
		while (iter.hasNext()) {
			TaxBreakDown taxBreakDown = iter.next();
			if (taxBreakDown.getTaxType().equals(TaxType.VAT)) {
				taxQuota = taxQuota + taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota();
			}
		}
		return taxQuota;
	}

	/**
	 * Insert or update account entry.
	 * 
	 * @param entry
	 *            the entry
	 * @param isNew
	 *            the is new
	 * 
	 * @return the account entry
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	public AccountEntry insertOrUpdateAccountEntry(AccountEntry entry)
			throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		return  (AccountEntry) entryBean.insertOrUpdate(entry);
	}

	/**
	 * Insert entry details.
	 * 
	 * @param entry
	 *            the entry
	 * @param account
	 *            the account
	 * @param series
	 *            invoice series
	 * @param number
	 *            invoice number
	 * @param invoiceTotal
	 *            the invoice total
	 * @param retentionTotal
	 *            the retention total
	 * @param taxQuota
	 *            the tax quota
	 * @param basesPerAccount
	 *            the bases per account
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	public void insertEntryDetails(AccountEntry entry, Account account, String concept,
			double invoiceTotal, double retentionTotal, double taxQuota,
			Map<Account, Double> basesPerAccount) throws ManagerBeanException {
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		// Primer Apunte
		AccountEntryDetail entryDetail = new AccountEntryDetail();
		entryDetail.setAccount(account);
		entryDetail.setAccountEntry(entry);
		if (basesPerAccount.size() == 1) {
			entryDetail.setBalancingAccount(basesPerAccount.keySet().iterator().next());
		}
		if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
			entryDetail.setDebit(invoiceTotal);
		}
		if (entry.getType().equals(AccountEntryType.PURCHASE_INVOICE)) {
			entryDetail.setCredit(invoiceTotal);
		}
		if (entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)) {
			entryDetail.setCredit(invoiceTotal);
		}
		entryDetail.setConcept(concept);
		entryDetailBean.insert(entryDetail);
		// Segundo Apunte(Mirar si hay q crearlo o no)
		entryDetail = new AccountEntryDetail();
		if (retentionTotal != 0) {
			if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
				entryDetail.setAccount(AccountUtil
						.obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT));
				entryDetail.setDebit(retentionTotal);
			} else {
				entryDetail.setAccount(AccountUtil
						.obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT));
				entryDetail.setCredit(retentionTotal);
			}
			entryDetail.setAccountEntry(entry);
			entryDetail.setBalancingAccount(account);
			entryDetail.setConcept(concept);
			entryDetailBean.insert(entryDetail);
		}
		// Tercer Apunte (Si I.V.A. es 0 no se crea)
		if (taxQuota != 0) {
			entryDetail = new AccountEntryDetail();
			if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
				entryDetail.setAccount(AccountUtil
						.obtainDefaultAccount(DefaultAccounts.CHARGE_VAT_ACCOUNT));
				entryDetail.setCredit(taxQuota);
			} else {
				entryDetail.setAccount(AccountUtil
						.obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT));
				entryDetail.setDebit(taxQuota);
			}
			entryDetail.setAccountEntry(entry);
			entryDetail.setBalancingAccount(account);
			entryDetail.setConcept(concept);
			entryDetailBean.insert(entryDetail);
		}
		// Cuarto Apunte (o varios Apuntes en funcion del Mapa de bases por
		// cuenta)
		Iterator<Account> iterator = basesPerAccount.keySet().iterator();
		while (iterator.hasNext()) {
			entryDetail = new AccountEntryDetail();
			entryDetail.setAccount(iterator.next());
			entryDetail.setAccountEntry(entry);
			entryDetail.setBalancingAccount(account);
			entryDetail.setConcept(concept);
			if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
				entryDetail
						.setCredit((basesPerAccount.get(entryDetail.getAccount())).doubleValue());
			} else {
				entryDetail.setDebit((basesPerAccount.get(entryDetail.getAccount())).doubleValue());
			}
			entryDetailBean.insert(entryDetail);
		}
	}

	/**
	 * Insert account entry invoice.
	 * 
	 * @param entry
	 *            the entry
	 * @param invoice
	 *            the invoice
	 * 
	 * @return the account entry invoice
	 * 
	 * @throws ManagerBeanException
	 *             the manager bean exception
	 */
	public AccountEntryInvoice insertAccountEntryInvoice(AccountEntry entry, Invoice invoice)
			throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		AccountEntryInvoice accountEntryInvoice = new AccountEntryInvoice();
		accountEntryInvoice.setInvoice(invoice);
		accountEntryInvoice.setAccountEntry(entry);
		return (AccountEntryInvoice) accountEntryInvoiceBean.insert(accountEntryInvoice);
	}

	@SuppressWarnings("unchecked")
	private AccountEntryInvoice obtainAccountEntryInvoice(Invoice invoice)
			throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean
				.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice
				.getId());
		Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (AccountEntryInvoice) iter.next();
		}
		return null;
	}

	private void removeAccountEntryInvoice(AccountEntryInvoice accEntryInvoice)
			throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		accountEntryInvoiceBean.remove(accEntryInvoice);
	}

}