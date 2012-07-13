package com.code.aon.account.bridge.writer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.account.bridge.ProductAccount;
import com.code.aon.account.bridge.enumeration.ProductAccountType;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.writer.pricing.AccountInvoicePriceStrategy;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

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

	public Invoice unrecordAndUpdateInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setUpdateEnabled(false);
		invoiceBean.restoreNullSubPOJOs(invoice);
		invoice = (Invoice) invoiceBean.update(invoice);
		unrecordInvoice(invoice);
		return invoice;
	}

	public void unrecordInvoice(Invoice invoice) throws ManagerBeanException {
		for (ITransferObject ito : obtainAccountEntryInvoices(invoice)) {
			AccountEntryInvoice accEntryInvoice = (AccountEntryInvoice)ito;
			checkPeriod(accEntryInvoice.getAccountEntry());
			removeAccountEntryInvoice(accEntryInvoice);
			removeInvoiceDetailAccounts(accEntryInvoice.getInvoice());
			removeInvoiceTaxAccounts(accEntryInvoice.getInvoice());
		}
	}

	private void checkPeriod(AccountEntry accountEntry) throws ManagerBeanException {
		Period period = accountEntry.getAccountPeriod();
		if (period.getStatus() == AccountPeriodStatus.CLOSED) {
			throw new ManagerBeanException("No se puede eliminar el apunte de un ejercicio cerrado (" + period.getName() + ").");
		}
		if (period.getStatus() == AccountPeriodStatus.INACTIVE) {
			throw new ManagerBeanException("No se puede eliminar el apunte de un ejercicio inactivo (" + period.getName() + ").");
		}
		if (period.getStatus() == AccountPeriodStatus.OPERATING) {
			throw new ManagerBeanException("No se puede eliminar el apunte, pues ya se ha hecho el asiento de explotación en el ejercicio (" + period.getName() + ").");
		}
	}
	
	public Invoice recordAndUpdateInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setStatus(InvoiceStatus.SCORED);
		invoice.setUpdateEnabled(false);
		invoiceBean.restoreNullSubPOJOs(invoice);
		invoice = (Invoice)invoiceBean.update(invoice);

		recordInvoice(invoice);
		return invoice;
	}
	
	public void recordInvoice(Invoice invoice) throws ManagerBeanException {
		recordInvoice(invoice, true);
	}
	
	public List<AccountEntryDetail> recordInvoice(Invoice invoice, boolean save) throws ManagerBeanException {
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(getAccountingUtil().obtainPeriod(invoice.getIssueDate()));
		entry.setEntryDate(invoice.getIssueDate());
		entry.setJournal(null);
		AccountEntryType accountEntryType = null;
		Account account = null;
		if (invoice.getType() == InvoiceType.SALES) {
			accountEntryType = AccountEntryType.SALES_INVOICE;
			account = getAccountBridgeUtil().obtainCustomerAccount(invoice.getRegistry());
		} else if (invoice.getType() == InvoiceType.PURCHASE) {
			accountEntryType = AccountEntryType.PURCHASE_INVOICE;
			account = getAccountBridgeUtil().obtainSupplierAccount(invoice.getRegistry());
		} else if (invoice.getType() == InvoiceType.EXPENSES) {
			accountEntryType = AccountEntryType.EXPENSE_INVOICE;
			account = getAccountBridgeUtil().obtainCreditorAccount(invoice.getRegistry());
		} else if (invoice.getType() == InvoiceType.UNDEDUCTIBLE) {
			accountEntryType = AccountEntryType.EXPENSES;
			account = getAccountBridgeUtil().obtainCreditorAccount(invoice.getRegistry());
		}
		entry.setType(accountEntryType);
		entry.setSecurityLevel(invoice.getSecurityLevel());
		if (save) {
			entry = insertOrUpdateAccountEntry(entry);
		}

		double total = getPriceStrategy().getTotalPrice(invoice, invoice);
		List<TaxBreakDown> taxBreakDownList = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
		Map<Account, Double> retentionQuotas = obtainRetentionQuotasPerAccount(taxBreakDownList, invoice);
		Map<Account, Double> taxQuotas = obtainTaxQuotasPerAccount(taxBreakDownList, invoice);
		Map<Account, Double> bases = obtainBasesPerAccount(invoice);
		List<AccountEntryDetail> details = insertEntryDetails(entry, account, 
					obtainConcept(invoice, total), invoice.getDocumentNumber(), 
					total, retentionQuotas, taxQuotas, bases, save);
		if (save) {
			insertAccountEntryInvoice(entry, invoice);	
		}
		return details;
	}
	
	public List<AccountEntryDetail> preRecordInvoice(Invoice invoice) throws ManagerBeanException {
		return recordInvoice(invoice, false);
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
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) iterator.next();
			Account account = null;
			if (invoiceDetail.getItem() != null) {
				Integer productId = invoiceDetail.getItem().getProduct().getId();
				ProductAccountType type = (invoice.getType().equals(InvoiceType.SALES)) ? ProductAccountType.SALES : ProductAccountType.PURCHASE;
				IManagerBean productAccountBean = BeanManager.getManagerBean(ProductAccount.class);
				criteria = new Criteria();
				criteria.addEqualExpression(productAccountBean.getFieldName(IEntityAlias.PRODUCT_ACCOUNT_PRODUCT_ID), productId);
				criteria.addEqualExpression(productAccountBean.getFieldName(IEntityAlias.PRODUCT_ACCOUNT_TYPE), type);
				Iterator<?> iter = productAccountBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					account = ((ProductAccount) iter.next()).getAccount();
				}
				if (account == null && invoiceDetail.getItem().getProduct().getType() == ProductType.EXPENSE) {
					throw new ManagerBeanException("El gasto \"" + invoiceDetail.getDescription() + "\" no tiene cuenta contable asociada.");
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

		if (basesPerAccount.size() > 1) {
			double diffBase = invoice.getTaxableBase();
			Iterator<Account> iter = basesPerAccount.keySet().iterator();
			while (iter.hasNext()) {
				Account account = (Account)iter.next();
				double base = CommonUtil.round(basesPerAccount.get(account).doubleValue());
				if (iter.hasNext()) {
					basesPerAccount.put(account, new Double(base));
					diffBase = CommonUtil.round(diffBase - base);
				} else {
					basesPerAccount.put(account, new Double(diffBase));
				}
			}
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
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_TAX_TYPE), taxBreakDown.getTaxType());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_PERCENTAGE), taxBreakDown.getTaxPercent());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_SURCHARGE), taxBreakDown.getSurchargePercent());
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
			criteria.addEqualExpression(appParamsBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), DefaultAccounts.SALES_ACCOUNT);
			Iterator<?> iter = appParamsBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				ApplicationParameter param = (ApplicationParameter) iter.next();
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				try {
					Integer accountId = Integer.parseInt(param.getValue());	
					salesDefaultAccount = (Account) accountBean.get(accountId);
				} catch (NumberFormatException e) {
					throw new ManagerBeanException("Revise el valor de la cuenta contable de ventas en los Parámetros Contables.");
				}
			}
		}
		if (salesDefaultAccount == null) {
			throw new ManagerBeanException("Revise el valor de la cuenta contable de ventas en los Parámetros Contables.");
		}
		return salesDefaultAccount;
	}

	private Account obtainPurchaseDefaultAccount() throws ManagerBeanException {
		if (purchaseDefaultAccount == null) {
			IManagerBean appParamsBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(appParamsBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), DefaultAccounts.PURCHASE_ACCOUNT);
			Iterator<?> iter = appParamsBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				ApplicationParameter param = (ApplicationParameter) iter.next();
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				try {
					Integer accountId = Integer.parseInt(param.getValue());	
					purchaseDefaultAccount = (Account) accountBean.get(accountId);
				} catch (NumberFormatException e) {
					throw new ManagerBeanException("Revise el valor de la cuenta contable de compras en los Parámetros Contables.");
				}
			}
		}
		if (purchaseDefaultAccount == null) {
			throw new ManagerBeanException("Revise el valor de la cuenta contable de compras en los Parámetros Contables.");
		}
		return purchaseDefaultAccount;
	}

	public String obtainConcept(String prefix, Invoice invoice) {
		return StringUtils.abbreviate(prefix + invoice.getReferenceCode(), 32) ; 
	}

	public String obtainConcept(Invoice invoice, double total) {
		String prefix = (invoice.getType().equals(InvoiceType.SALES)) ? N_FRA : S_FRA;
		if (total < 0) {
			prefix += " " + ABONO;
		}
		prefix += ": ";
		return obtainConcept(prefix, invoice);
	}

	public AccountEntry insertOrUpdateAccountEntry(AccountEntry entry) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		return  (AccountEntry) entryBean.insertOrUpdate(entry);
	}

	public List<AccountEntryDetail> insertEntryDetails(AccountEntry entry, Account account, 
			String concept,	String documentNumber, double invoiceTotal, 
			Map<Account, Double> retentionQuotasPerAccount, Map<Account, Double> taxQuotasPerAccount, 
			Map<Account, Double> basesPerAccount, boolean save) throws ManagerBeanException {
		List<AccountEntryDetail> details = new LinkedList<AccountEntryDetail>();
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
		} else {
			entryDetail.setCredit(invoiceTotal);
		}
		entryDetail.setConcept(concept);
		entryDetail.setDocumentNumber(documentNumber);
		if (save) {
			entryDetail = (AccountEntryDetail) entryDetailBean.insert(entryDetail);	
		}
		details.add(entryDetail);
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
					entryDetail.setDocumentNumber(documentNumber);
					if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
						entryDetail.setDebit(retentionQuota);
					} else {
						entryDetail.setCredit(retentionQuota);
					}
					if (save) {
						entryDetail = (AccountEntryDetail) entryDetailBean.insert(entryDetail);	
					}
					details.add(entryDetail);
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
					entryDetail.setDocumentNumber(documentNumber);
					if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
						entryDetail.setCredit(taxQuota);
					} else {
						entryDetail.setDebit(taxQuota);
					}
					if (save) {
						entryDetail = (AccountEntryDetail) entryDetailBean.insert(entryDetail);	
					}
					details.add(entryDetail);
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
					entryDetail.setDocumentNumber(documentNumber);
					if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
						entryDetail.setCredit(base);
					} else {
						entryDetail.setDebit(base);
					}
					if (save) {
						entryDetail = (AccountEntryDetail) entryDetailBean.insert(entryDetail);	
					}
					details.add(entryDetail);
				}
			}
		}
		return details;
	}

	public AccountEntryInvoice insertAccountEntryInvoice(AccountEntry entry, Invoice invoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		AccountEntryInvoice accountEntryInvoice = new AccountEntryInvoice();
		accountEntryInvoice.setInvoice(invoice);
		accountEntryInvoice.setAccountEntry(entry);
		return (AccountEntryInvoice) accountEntryInvoiceBean.insert(accountEntryInvoice);
	}

	private List<ITransferObject> obtainAccountEntryInvoices(Invoice invoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
		return accountEntryInvoiceBean.getList(criteria);
	}

	private void removeAccountEntryInvoice(AccountEntryInvoice accEntryInvoice) throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		accountEntryInvoiceBean.remove(accEntryInvoice);
	}

	private void removeInvoiceDetailAccounts(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailAccountBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceDetailAccountBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)iterator.next();
			invoiceDetailAccountBean.remove(invoiceDetailAccount);
		}
	}

	private void removeInvoiceTaxAccounts(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceTaxAccountBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxAccountBean.getFieldName(IEntityAlias.INVOICE_TAX_ACCOUNT_INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<?> iterator = invoiceTaxAccountBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceTaxAccount invoiceTaxAccount = (InvoiceTaxAccount)iterator.next();
			invoiceTaxAccountBean.remove(invoiceTaxAccount);
		}
	}

}