package com.code.aon.account.bridge.writer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.writer.pricing.AccountInvoicePriceStrategy;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
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

	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";

	private Account salesDefaultAccount;
	private Account purchaseDefaultAccount;

	private IPriceStrategy priceStrategy;
	private AccountBridgeUtil accountBridgeUtil;
	private AccountingUtil accountingUtil;

	private IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new AccountInvoicePriceStrategy();
		}
		return priceStrategy;
	}
	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}
	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	public void unrecordAndUpdateInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.PENDING);
		if (invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null) {
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);

		unrecordInvoice(invoice);
	}

	public void unrecordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntryInvoice accEntryInvoice = obtainAccountEntryInvoice(invoice);
		if (accEntryInvoice != null) {
			removeAccountEntryInvoice(accEntryInvoice);
			removeInvoiceDetailAccounts(accEntryInvoice.getInvoice());
			removeInvoiceTaxAccounts(accEntryInvoice.getInvoice());
		}
	}

	public void recordAndUpdateInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.SCORED);
		if (invoice.getRegistryAddress() != null && invoice.getRegistryAddress().getId() == null) {
			invoice.setRegistryAddress(null);
		}
		invoiceBean.update(invoice);

		recordInvoice(invoice);	
	}
	
	public void recordInvoice(Invoice invoice) throws ManagerBeanException {
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(getAccountingUtil().obtainPeriod(invoice.getIssueDate()).getId());
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
		Account account = (invoice.getType().equals(InvoiceType.SALES) ? 
				getAccountBridgeUtil().obtainCustomerAccount(invoice.getRegistry()) : 
				getAccountBridgeUtil().obtainSupplierAccount(invoice.getRegistry()));
		double total = getPriceStrategy().getTotalPrice(invoice, invoice);
		List<TaxBreakDown> taxBreakDownList = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
		Map<Account, Double> retentionQuotas = obtainRetentionQuotasPerAccount(taxBreakDownList, invoice);
		Map<Account, Double> taxQuotas = obtainTaxQuotasPerAccount(taxBreakDownList, invoice);
		Map<Account, Double> bases = obtainBasesPerAccount(invoice);
		insertEntryDetails(entry, account, obtainConcept(invoice, total), total, retentionQuotas, taxQuotas, bases);
		insertAccountEntryInvoice(entry, invoice);
	}

	private Map<Account, Double> obtainRetentionQuotasPerAccount(List<TaxBreakDown> taxBreakDownList, Invoice invoice) throws ManagerBeanException {
		TaxRecordingTo recordingTo = new TaxRecordingTo();
		Iterator<TaxBreakDown> iterator = taxBreakDownList.iterator();
		while (iterator.hasNext()) {
			TaxBreakDown taxBreakDown = iterator.next();
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				recordingTo.addTaxQuotaAccount(taxBreakDown.getAccount(), new Double(taxBreakDown.getTaxQuota()+taxBreakDown.getSurchargeQuota()));
				insertInvoiceTaxAccount(invoice, taxBreakDown);
			}
		}
		return recordingTo.getTaxQuotaAccountMap();
	}

	private Map<Account, Double> obtainTaxQuotasPerAccount(List<TaxBreakDown> taxBreakDownList, Invoice invoice) throws ManagerBeanException {
		TaxRecordingTo recordingTo = new TaxRecordingTo();
		Iterator<TaxBreakDown> iterator = taxBreakDownList.iterator();
		while (iterator.hasNext()) {
			TaxBreakDown taxBreakDown = iterator.next();
			if (!taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				recordingTo.addTaxQuotaAccount(taxBreakDown.getAccount(), new Double(taxBreakDown.getTaxQuota()+taxBreakDown.getSurchargeQuota()));
				insertInvoiceTaxAccount(invoice, taxBreakDown);
			}
		}
		return recordingTo.getTaxQuotaAccountMap();
	}

	private Map<Account, Double> obtainBasesPerAccount(Invoice invoice) throws ManagerBeanException {
		Map<Account, Double> basesPerAccount = new HashMap<Account, Double>();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) iterator.next();
			Account account = null;
			if (invoiceDetail.getItem() != null) {
				Integer productId = invoiceDetail.getItem().getProduct().getId();
				ProductAccountType type = (invoice.getType().equals(InvoiceType.SALES)) ? ProductAccountType.SALES : ProductAccountType.PURCHASE;
				IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				criteria = new Criteria();
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_PRODUCT_ID), productId);
				criteria.addEqualExpression(productAccountBean.getFieldName(IAccountBridgeAlias.PRODUCT_ACCOUNT_TYPE), type);
				Iterator<?> iter = productAccountBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					account = ((ProductAccount) iter.next()).getAccount();
				}
			}
			if (account == null) {
				account = (invoice.getType().equals(InvoiceType.SALES)) ? obtainSalesDefaultAccount() : obtainPurchaseDefaultAccount();
			}
			double base = invoiceDetail.getTaxableBase();
			base += (basesPerAccount.containsKey(account)) ? basesPerAccount.get(account).doubleValue() : 0;
			basesPerAccount.put(account, new Double(base));
			insertInvoiceDetailAccount(invoiceDetail, account);
		}
		return basesPerAccount;
	}

	private void insertInvoiceDetailAccount(InvoiceDetail invoiceDetail, Account account) throws ManagerBeanException {
		IManagerBean invoiceDetailAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
		invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
		invoiceDetailAccount.setAccount(account);
		invoiceDetailAccountBean.insert(invoiceDetailAccount);
	}

	private void insertInvoiceTaxAccount(Invoice invoice, TaxBreakDown taxBreakDown) throws ManagerBeanException {
		IManagerBean invoiceTaxAccountBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_TAX_TYPE), taxBreakDown.getTaxType());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_PERCENTAGE), taxBreakDown.getTaxPercent());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_SURCHARGE), taxBreakDown.getSurchargePercent());
		Iterator<?> iterator = invoiceTaxBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceTax invoiceTax = (InvoiceTax)iterator.next();

			InvoiceTaxAccount invoiceTaxAccount = new InvoiceTaxAccount();
			invoiceTaxAccount.setInvoiceTax(invoiceTax);
			invoiceTaxAccount.setAccount(taxBreakDown.getAccount());
			invoiceTaxAccountBean.insert(invoiceTaxAccount);
		}
	}

	private Account obtainSalesDefaultAccount() throws ManagerBeanException {
		if (salesDefaultAccount == null) {
			IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(appParamsBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), DefaultAccounts.SALES_ACCOUNT);
			Iterator<?> iter = appParamsBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				ApplicationParameter param = (ApplicationParameter) iter.next();
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), param.getValue());
				Iterator<?> accountIter = accountBean.getList(accountCriteria).iterator();
				if (accountIter.hasNext()) {
					salesDefaultAccount = (Account) accountIter.next();
				}
			}
		}
		return salesDefaultAccount;
	}

	private Account obtainPurchaseDefaultAccount() throws ManagerBeanException {
		if (purchaseDefaultAccount == null) {
			IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(appParamsBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), DefaultAccounts.PURCHASE_ACCOUNT);
			Iterator<?> iter = appParamsBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				ApplicationParameter param = (ApplicationParameter) iter.next();
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), param.getValue());
				Iterator<?> accountIter = accountBean.getList(accountCriteria).iterator();
				if (accountIter.hasNext()) {
					purchaseDefaultAccount = (Account) accountIter.next();
				}
			}
		}
		return purchaseDefaultAccount;
	}

	public String obtainConcept(String prefix, Invoice invoice) {
		return StringUtils.abbreviate(prefix + " " + invoice.getReferenceCode(), 32) ; 
	}

	public String obtainConcept(Invoice invoice, double total) {
		String prefix = (invoice.getType().equals(InvoiceType.SALES)) ? N_FRA : S_FRA;
		if (total < 0) {
			prefix += " " + ABONO;
		}
		prefix += ": ";
		return obtainConcept(prefix, invoice);
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
	public AccountEntry insertOrUpdateAccountEntry(AccountEntry entry) throws ManagerBeanException {
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
	public void insertEntryDetails(AccountEntry entry, Account account, String concept,	double invoiceTotal,
			Map<Account, Double> retentionQuotasPerAccount, Map<Account, Double> taxQuotasPerAccount, 
			Map<Account, Double> basesPerAccount) throws ManagerBeanException {
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		// Primer Apunte (Cliente o Proveedor)
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
		// Segundo Apunte (Retenciones)
		if (retentionQuotasPerAccount != null) {
			Iterator<Account> iterator = retentionQuotasPerAccount.keySet().iterator();
			while (iterator.hasNext()) {
				Account retentionAccount = iterator.next();
				double retentionQuota = (retentionQuotasPerAccount.get(retentionAccount)).doubleValue();
				if (retentionQuota != 0) {
					entryDetail = new AccountEntryDetail();
					entryDetail.setAccount(retentionAccount);
					entryDetail.setAccountEntry(entry);
					entryDetail.setBalancingAccount(account);
					entryDetail.setConcept(concept);
					if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
						entryDetail.setDebit(retentionQuota);
					} else {
						entryDetail.setCredit(retentionQuota);
					}
					entryDetailBean.insert(entryDetail);
				}
			}
		}
		// Tercer Apunte (I.V.A.)
		if (taxQuotasPerAccount != null) {
			Iterator<Account> iterator = taxQuotasPerAccount.keySet().iterator();
			while (iterator.hasNext()) {
				Account taxAccount = iterator.next();
				double taxQuota = (taxQuotasPerAccount.get(taxAccount)).doubleValue();
				if (taxQuota != 0) {
					entryDetail = new AccountEntryDetail();
					entryDetail.setAccount(taxAccount);
					entryDetail.setAccountEntry(entry);
					entryDetail.setBalancingAccount(account);
					entryDetail.setConcept(concept);
					if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
						entryDetail.setCredit(taxQuota);
					} else {
						entryDetail.setDebit(taxQuota);
					}
					entryDetailBean.insert(entryDetail);
				}
			}
		}
		// Cuarto Apunte (Ventas o Compras, puede haber varios apuntes en funcion de las cuentas de los productos de la factura)
		if (basesPerAccount.size() > 0) {
			Iterator<Account> iterator = basesPerAccount.keySet().iterator();
			while (iterator.hasNext()) {
				Account baseAccount = iterator.next();
				double base = (basesPerAccount.get(baseAccount)).doubleValue();
				if (base != 0) {
					entryDetail = new AccountEntryDetail();
					entryDetail.setAccount(baseAccount);
					entryDetail.setAccountEntry(entry);
					entryDetail.setBalancingAccount(account);
					entryDetail.setConcept(concept);
					if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
						entryDetail.setCredit(base);
					} else {
						entryDetail.setDebit(base);
					}
					entryDetailBean.insert(entryDetail);
				}
			}
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
	public AccountEntryInvoice insertAccountEntryInvoice(AccountEntry entry, Invoice invoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		AccountEntryInvoice accountEntryInvoice = new AccountEntryInvoice();
		accountEntryInvoice.setInvoice(invoice);
		accountEntryInvoice.setAccountEntry(entry);
		return (AccountEntryInvoice) accountEntryInvoiceBean.insert(accountEntryInvoice);
	}

	private AccountEntryInvoice obtainAccountEntryInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = accountEntryInvoiceBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (AccountEntryInvoice) iterator.next();
		}
		return null;
	}

	private void removeAccountEntryInvoice(AccountEntryInvoice accEntryInvoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		accountEntryInvoiceBean.remove(accEntryInvoice);
	}

	private void removeInvoiceDetailAccounts(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceDetailAccountBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)iterator.next();
			invoiceDetailAccountBean.remove(invoiceDetailAccount);
		}
	}

	private void removeInvoiceTaxAccounts(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceTaxAccountBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_TAX_ACCOUNT_INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceTaxAccountBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceTaxAccount invoiceTaxAccount = (InvoiceTaxAccount)iterator.next();
			invoiceTaxAccountBean.remove(invoiceTaxAccount);
		}
	}

}