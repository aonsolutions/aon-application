package com.code.aon.account.bridge.writer;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.InvoiceTaxAccount;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.writer.pricing.AccountInvoicePriceStrategy;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.AmortizationInvoice;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountEntryInvoiceWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String N_FRA = "N/Fra";
	private static final String S_FRA = "S/Fra";
	private static final String ABONO = "ABONO";

	private Account salesDefaultAccount;
	private Account purchaseDefaultAccount;
	private Account prepaymentDefaultAccount;

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
			getAccountingUtil().checkPeriod(accEntryInvoice.getAccountEntry());
			removeAccountEntryInvoice(accEntryInvoice);
			removeInvoiceDetailAccounts(accEntryInvoice.getInvoice());
			removeInvoiceTaxAccounts(accEntryInvoice.getInvoice());
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
		if (invoice.getIssueDate() == null) {
			throw new ManagerBeanException("La fecha de la factura no puede estar vacia.");
		}
		Date entryDate = invoice.getIssueDate();
		entry.setAccountPeriod(getAccountingUtil().obtainPeriod(entryDate));
		entry.setActivity((invoice.getActivity() != null && invoice.getActivity().getId() != null) ? invoice.getActivity() : null);
		entry.setEntryDate(entryDate);
		entry.setJournal(null);
		AccountEntryType accountEntryType = null;
		Account account = null;
		if (invoice.isSales()) {
			accountEntryType = AccountEntryType.SALES_INVOICE;
			account = getAccountBridgeUtil().obtainCustomerAccount(invoice.getRegistry());
		} else if (invoice.isPurchase()) {
			accountEntryType = AccountEntryType.PURCHASE_INVOICE;
			account = getAccountBridgeUtil().obtainSupplierAccount(invoice.getRegistry());
			
			
			// ******************************************************
			// ********************* ÑAPA PADRE ********************* 
			// ******************************************************
			if (account == null) {
				account = getAccountBridgeUtil().obtainCreditorAccount(invoice.getRegistry());
				if (account == null) {
					throw new ManagerBeanException("El proveedor " + invoice.getRegistry().getName() + " no existe.");
				} else {
					changeType( invoice,  InvoiceType.EXPENSES);
				}
			}
			// ******************************************************
			
		} else if (invoice.isExpense()) {
			accountEntryType = AccountEntryType.EXPENSE_INVOICE;
			account = getAccountBridgeUtil().obtainCreditorAccount(invoice.getRegistry());

			// ******************************************************
			// ********************* ÑAPA PADRE ********************* 
			// ******************************************************
			if (account == null) {
				account = getAccountBridgeUtil().obtainSupplierAccount(invoice.getRegistry());
				if (account == null) {
					throw new ManagerBeanException("El acreedor " + invoice.getRegistry().getName() + " no existe.");
				} else {
					changeType( invoice,  InvoiceType.PURCHASE);
				}
			}
			// ******************************************************

		} else if (invoice.isUndeductible()) {
			accountEntryType = AccountEntryType.EXPENSES;
			account = getAccountBridgeUtil().obtainCreditorAccount(invoice.getRegistry());
			if (account == null) {
				throw new ManagerBeanException("El acreedor " + invoice.getRegistry().getName() + " no existe.");
			}
			// ******************************************************
		}
		entry.setType(accountEntryType);
		entry.setSecurityLevel(invoice.getSecurityLevel());
		if (save) {
			entry = insertOrUpdateAccountEntry(entry);
		}

		double total = getPriceStrategy().getTotalPrice(invoice, invoice);
		boolean ignoreTaxFree = !invoice.isSales() && (invoice.isIntracommunity() || invoice.isOtherISP() || (!invoice.isNational() && invoice.isService()));
		List<TaxBreakDown> taxBreakDownList = getPriceStrategy().getTaxBreakDowns(invoice, invoice, ignoreTaxFree);
		Map<Account, Double> retentionQuotas = obtainRetentionQuotasPerAccount(taxBreakDownList, invoice, save);
		Map<Account, Double> taxQuotas = obtainTaxQuotasPerAccount(taxBreakDownList, invoice, ignoreTaxFree, save);
		Map<Account, Double> bases = obtainBasesPerAccount(invoice, save);
		List<AccountEntryDetail> details = insertEntryDetails(entry, account, 
					obtainConcept(invoice, total), invoice.getDocumentNumber(), 
					total, retentionQuotas, taxQuotas, bases, save);
		if (save) {
			insertAccountEntryInvoice(entry, invoice);	
		}
		return details;
	}
	
	private void changeType(Invoice invoice, InvoiceType type) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setSkipCalculateMainActivity(true);
		invoice.setUpdateEnabled(false);
		invoice.setUpdateDetails(false);
		invoice.setType(type);
		invoiceBean.update(invoice); 
	}
	
	public List<AccountEntryDetail> preRecordInvoice(Invoice invoice) throws ManagerBeanException {
		return recordInvoice(invoice, false);
	}
	
	private Map<Account, Double> obtainRetentionQuotasPerAccount(List<TaxBreakDown> taxBreakDownList, Invoice invoice, boolean save) throws ManagerBeanException {
		TaxRecordingTo recordingTo = new TaxRecordingTo();
		for (TaxBreakDown taxBreakDown : taxBreakDownList) {
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				recordingTo.addTaxQuotaAccount(taxBreakDown.getAccount(), CommonUtil.round(taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota()));
				if (save) {
					insertInvoiceTaxAccount(invoice, taxBreakDown, taxBreakDown.getAccount());
				}
			}
		}
		return recordingTo.getTaxQuotaAccountMap();
	}

	private Map<Account, Double> obtainTaxQuotasPerAccount(List<TaxBreakDown> taxBreakDownList, Invoice invoice, boolean ignoreTaxFree, boolean save) throws ManagerBeanException {
		TaxRecordingTo recordingTo = new TaxRecordingTo();
		for (TaxBreakDown taxBreakDown : taxBreakDownList) {
			if (!taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				if ( AonMathUtils.isZero(taxBreakDown.getSurchargeQuota()) ) {
					double quota = CommonUtil.round(taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota());
					if (taxBreakDown.getDeductibleQuota() != quota) {
						quota = taxBreakDown.getDeductibleQuota();
					}
					if (taxBreakDown.getAccount() == null) {
						throw new ManagerBeanException("No se ha definido cuenta contable para el IVA");
					}
					recordingTo.addTaxQuotaAccount(taxBreakDown.getAccount(), quota);
					if (save) {
						insertInvoiceTaxAccount(invoice, taxBreakDown, taxBreakDown.getAccount());
					}
					if (ignoreTaxFree) {
						recordingTo.addTaxQuotaAccount(taxBreakDown.getBalancingAccount(), CommonUtil.round(quota * (-1)));
						if (save) {
							insertInvoiceTaxAccount(invoice, taxBreakDown, taxBreakDown.getBalancingAccount());
						}
					} else {
						if (taxBreakDown.getDeductibleQuota() != CommonUtil.round(taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota())) {
							Account account = AccountingUtil.obtainDefaultAccount(AppParam.ACC_VAT_NEGATIVE_ADJUST_ACC);
							if (account == null) {
								throw new ManagerBeanException("Falta definir la Cuenta de ajustes negativos por IVA");
							}
							quota = CommonUtil.round(taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota() - taxBreakDown.getDeductibleQuota());
							recordingTo.addTaxQuotaAccount(account, quota);
							if (save) {
								insertInvoiceTaxAccount(invoice, taxBreakDown, account);
							}
						}
					}
				} else {
					double quota = CommonUtil.round(taxBreakDown.getTaxQuota() + taxBreakDown.getSurchargeQuota());
					if (taxBreakDown.getAccount() == null) {
						throw new ManagerBeanException("No se ha definido cuenta contable para el IVA");
					}
					recordingTo.addTaxQuotaAccount(taxBreakDown.getAccount(), quota);
					if (save) {
						insertInvoiceTaxAccount(invoice, taxBreakDown, taxBreakDown.getAccount());
					}
					if (ignoreTaxFree) {
						recordingTo.addTaxQuotaAccount(taxBreakDown.getBalancingAccount(), CommonUtil.round(quota * (-1)));
						if (save) {
							insertInvoiceTaxAccount(invoice, taxBreakDown, taxBreakDown.getBalancingAccount());
						}
					} 
				}
			}
		}
		return recordingTo.getTaxQuotaAccountMap();
	}
	
	private Map<Account, Double> obtainBasesPerAccount(Invoice invoice, boolean save) throws ManagerBeanException {
		Map<Account, Double> basesPerAccount = new HashMap<>();
		double diffBase = invoice.getTaxableBase();
		if (invoice.isInvestment()) {
			if (!invoice.isSales()) {
				fillPurchaseBasesPerAccountFromAmortization(invoice, basesPerAccount);	
			} else {
				fillSaleBasesPerAccountFromAmortization(invoice, basesPerAccount);
			}
		} else {
			diffBase = fillBasesPerAccountFromInvoiceDetail(invoice, basesPerAccount, save);
		}
		if (basesPerAccount.size() > 1) {
			Iterator<Account> iter = basesPerAccount.keySet().iterator();
			while (iter.hasNext()) {
				Account account = iter.next();
				double base = CommonUtil.round(basesPerAccount.get(account).doubleValue());
				if (iter.hasNext()) {
					basesPerAccount.put(account, CommonUtil.round(base, 4));
					diffBase = CommonUtil.round(diffBase - base);
				} else {
					basesPerAccount.put(account, CommonUtil.round(diffBase, 4));
				}
			}
		}
		return basesPerAccount;
	}

	private double fillBasesPerAccountFromInvoiceDetail(Invoice invoice, Map<Account, Double> basesPerAccount, boolean save) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		List<ITransferObject> list = invoiceDetailBean.getList(criteria);
		double retBase = 0.0;
		boolean isTedi = false;
		boolean hasPrepayment = false;
		for (ITransferObject to : list) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) to;
			isTedi = isTedi || invoiceDetail.getSource() == InvoiceSource.TEDI;
			hasPrepayment = hasPrepayment || invoiceDetail.isPrepayment();
			Account account = null;
			if (invoiceDetail.getItem() != null) {
				account = (invoice.isSales()) 
					? invoiceDetail.getItem().getProduct().getSalesAccount() 
					: invoiceDetail.getItem().getProduct().getPurchaseAccount();
				if (account == null && invoiceDetail.getItem().getProduct().getType() == ProductType.EXPENSE) {
					throw new ManagerBeanException("El gasto \"" + invoiceDetail.getDescription() + "\" no tiene cuenta contable asociada.");
				}
			}
			if (account == null) {
				if (invoiceDetail.isPrepayment()) {
					account = obtainPrepaymentDefaultAccount();
				} else {
					if (invoiceDetail.getSource() == InvoiceSource.TEDI) {
						account = obtainAccountFromInvoiceDetailAccount(invoiceDetail);
					} 
					if (account == null) {		
						if (invoice.isSales()) {
							account = obtainSalesDefaultAccount();
						} else if (invoice.isPurchase()) {
							account = obtainPurchaseDefaultAccount();	
						} else {
							account = obtainAccountFromInvoiceDetailAccount(invoiceDetail);
							if (account == null) {
								account = obtainPurchaseDefaultAccount();
							}
						}
					}
				}
			}
			retBase = CommonUtil.round(retBase + invoiceDetail.getTaxableBase(), 4);
			double base = invoiceDetail.getTaxableBase();
			base += (basesPerAccount.containsKey(account)) ? basesPerAccount.get(account).doubleValue() : 0;
			basesPerAccount.put(account, CommonUtil.round(base, 4));
			if (save) {
				insertInvoiceDetailAccount(invoiceDetail, account);
			}
		}
		return (isTedi && hasPrepayment) 
			?retBase
			:invoice.getTaxableBase();
	}
	
	private Account obtainAccountFromInvoiceDetailAccount(InvoiceDetail invoiceDetail) throws ManagerBeanException {
		IManagerBean invoiceDetailAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria crit = new Criteria();
		crit.addEqualExpression(invoiceDetailAccountBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID), invoiceDetail.getId());
		List<ITransferObject> listAccounts = invoiceDetailAccountBean.getList(crit);
		if ( AonCollectionUtils.isNotEmpty(listAccounts) ) {
			InvoiceDetailAccount toAccount = (InvoiceDetailAccount) listAccounts.get(0);
			return toAccount.getAccount();
		}
		return null;
	}
	
	private void fillPurchaseBasesPerAccountFromAmortization(Invoice invoice,Map<Account, Double> basesPerAccount) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AmortizationInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_SALES), false);
		List<ITransferObject> list = bean.getList(criteria);
		// De momento solo se puede vincular una ficha de amortizacion a una factura.
		if (AonCollectionUtils.isNotEmpty( list ) ) {
			AmortizationInvoice ai = (AmortizationInvoice) list.get(0);
			basesPerAccount.put(ai.getAmortization().getFixedAssetAccount(), invoice.getTaxableBase());		
		}
	}
	
	private void fillSaleBasesPerAccountFromAmortization(Invoice invoice,Map<Account, Double> basesPerAccount) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AmortizationInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.AMORTIZATION_INVOICE_SALES), true);
		List<ITransferObject> list = bean.getList(criteria);
		// De momento solo se puede vincular una factura a una ficha de amortizacion.
		if (AonCollectionUtils.isNotEmpty(list) ) {
			AmortizationInvoice ai = (AmortizationInvoice) list.get(0);
			double amount = ai.getAmortization().getAmount();
			basesPerAccount.put(ai.getAmortization().getFixedAssetAccount(), amount);
			
			IManagerBean detailBean = BeanManager.getManagerBean(AmortizationDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID), ai.getAmortization().getId());
			criteria.addOrder(detailBean.getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE), false);
			list = detailBean.getList(criteria);
			double accumulated = 0.0;
			if ( AonCollectionUtils.isNotEmpty( list ) ) {
				AmortizationDetail ad = (AmortizationDetail) list.get(0);
				accumulated = ad.getAccumulated();
				basesPerAccount.put(ai.getAmortization().getAccumulatedAccount(), CommonUtil.round(accumulated*(-1),2));	
			}
			double profitLoss = CommonUtil.round((invoice.getTaxableBase() + accumulated) - amount,2);
			Account pl = null;
			if (profitLoss != 0.0) {
				if (profitLoss > 0) {
					pl = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_ASSET_PROFIT_ACC);
					if (pl == null) {
						throw new ManagerBeanException("No se ha definido cuenta contable para beneficios por venta de inmovilizado");
					}
					
				} else {
					pl = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_ASSET_LOST_ACC);
					if (pl == null) {
						throw new ManagerBeanException("No se ha definido cuenta contable para pérdidas por venta de inmovilizado");
					}
				}
				basesPerAccount.put(pl , profitLoss);
			}
		}
	}
	
	private void insertInvoiceDetailAccount(InvoiceDetail invoiceDetail, Account account) throws ManagerBeanException {
		if (account == null) return;
		IManagerBean invoiceDetailAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		boolean exists = false;
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailAccountBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID), invoiceDetail.getId());
		List<ITransferObject> list = invoiceDetailAccountBean.getList(criteria);
		if (AonCollectionUtils.isNotEmpty(list)) {
			for (ITransferObject to : list) {
				InvoiceDetailAccount ida = (InvoiceDetailAccount) to;
				if (ida.getAccount() != null && ida.getAccount().getId().equals(account.getId())) {
					exists = true;
				} else {
					invoiceDetailAccountBean.remove(ida);
				}
			}
		}

		if (!exists) {
			InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
			invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
			invoiceDetailAccount.setAccount(account);
			invoiceDetailAccountBean.insert(invoiceDetailAccount);
		}
	}

	private void insertInvoiceTaxAccount(Invoice invoice, TaxBreakDown taxBreakDown, Account account) throws ManagerBeanException {
		IManagerBean invoiceTaxAccountBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_TAX_TYPE), taxBreakDown.getTaxType());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_PERCENTAGE), taxBreakDown.getTaxPercent());
		criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_SURCHARGE), taxBreakDown.getSurchargePercent());
		List<ITransferObject> list = invoiceTaxBean.getList(criteria);
		for (ITransferObject to : list) {
			InvoiceTax invoiceTax = (InvoiceTax) to;
			InvoiceTaxAccount invoiceTaxAccount = new InvoiceTaxAccount();
			invoiceTaxAccount.setInvoiceTax(invoiceTax);
			invoiceTaxAccount.setAccount(account);
			invoiceTaxAccountBean.insert(invoiceTaxAccount);
		}
	}

	private Account obtainSalesDefaultAccount() throws ManagerBeanException {
		if (salesDefaultAccount == null) {
			salesDefaultAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_SALES_ACC);
		}
		if (salesDefaultAccount == null) {
			throw new ManagerBeanException("Revise el valor de la cuenta contable de ventas en los Parámetros Contables.");
		}
		return salesDefaultAccount;
	}
	
	private Account obtainPurchaseDefaultAccount() throws ManagerBeanException {
		if (purchaseDefaultAccount == null) {
			purchaseDefaultAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PURCHASE_ACC);
		}
		if (purchaseDefaultAccount == null) {
			throw new ManagerBeanException("Revise el valor de la cuenta contable de compras en los Parámetros Contables.");
		}
		return purchaseDefaultAccount;
	}

	private Account obtainPrepaymentDefaultAccount() throws ManagerBeanException {
		if (prepaymentDefaultAccount == null) {
			prepaymentDefaultAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PREPAYMENT_ACC);
		}
		return prepaymentDefaultAccount;
	}

	public String obtainConcept(String prefix, Invoice invoice) {
		return StringUtils.abbreviate(prefix + invoice.getReferenceCode(), 64) ; 
	}

	public String obtainConcept(Invoice invoice, double total) {
		String prefix = (invoice.isSales()) ? N_FRA : S_FRA;
		if (total < 0) {
			prefix += " " + ABONO;
		}
		prefix += ": ";
		return obtainConcept(prefix, invoice);
	}

	public AccountEntry insertOrUpdateAccountEntry(AccountEntry entry) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		return (AccountEntry)entryBean.insertOrUpdate(entry);
	}

	public List<AccountEntryDetail> insertEntryDetails(AccountEntry entry, Account account, 
			String concept,	String documentNumber, double invoiceTotal, 
			Map<Account, Double> retentionQuotasPerAccount, Map<Account, Double> taxQuotasPerAccount, 
			Map<Account, Double> basesPerAccount, boolean save) throws ManagerBeanException {
		List<AccountEntryDetail> details = new LinkedList<>();
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
		List<ITransferObject> list = invoiceDetailAccountBean.getList(criteria);
		for (ITransferObject to : list) {
			InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount) to;
			invoiceDetailAccountBean.remove(invoiceDetailAccount);
		}
	}

	private void removeInvoiceTaxAccounts(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceTaxAccountBean = BeanManager.getManagerBean(InvoiceTaxAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceTaxAccountBean.getFieldName(IEntityAlias.INVOICE_TAX_ACCOUNT_INVOICE_TAX_INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		List<ITransferObject> list = invoiceTaxAccountBean.getList(criteria);
		for (ITransferObject to : list) {
			InvoiceTaxAccount invoiceTaxAccount = (InvoiceTaxAccount) to;
			invoiceTaxAccountBean.remove(invoiceTaxAccount);
		}
	}

}