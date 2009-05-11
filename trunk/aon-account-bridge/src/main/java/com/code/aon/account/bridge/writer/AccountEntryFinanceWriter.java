package com.code.aon.account.bridge.writer;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;

/**
 * The Class AccountEntryFinanceWriter.
 */
public class AccountEntryFinanceWriter {

	private static final String C_FRA = "Cobro Fra: ";
	private static final String P_FRA = "Pago Fra: ";
	private static final String A_FRA = "Abono Fra: ";
	private static final String D_FRA = "Dev. Fra: ";

	@SuppressWarnings("unchecked")
	public AccountEntry recordFBatchDetails(FinanceRecordingTo to, FinanceBatch fbatch) throws ManagerBeanException {
		AccountEntry entry = createAccountEntry(to);
		Account bankAccount = (to.getRegistryBank() != null?AccountUtil.obtainRBankAccount(to.getRegistryBank()):AccountUtil.obtainCashAccount());

		Iterator iterator = to.getFBatchDetailList().iterator();
		double amount = 0.0;
		while (iterator.hasNext()) {
			FinanceBatchDetail fbatchDetail = (FinanceBatchDetail)iterator.next();
			amount += fbatchDetail.getAmount();
			insertFBatchDetailEntryDetail(bankAccount, fbatchDetail, entry);
		}
		insertFBatchDetailLastEntryDetail(bankAccount, entry, fbatch.getDescription(), amount);
		return entry;
	}

	private void insertFBatchDetailEntryDetail(Account bankAccount, FinanceBatchDetail fbatchDetail, AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		Account registryAccount = null;
		if (fbatchDetail.getFinance().getInvoice().getType().equals(InvoiceType.SALES)) {
			registryAccount = AccountUtil.obtainCustomerAccount(fbatchDetail.getFinance().getRegistry());
			detail.setCredit(fbatchDetail.getAmount());
		} else if(fbatchDetail.getFinance().getInvoice().getType().equals(InvoiceType.PURCHASE)) {
			registryAccount = AccountUtil.obtainSupplierAccount(fbatchDetail.getFinance().getRegistry());
			detail.setDebit(fbatchDetail.getAmount());
		} else {
			registryAccount = AccountUtil.obtainCreditorAccount(fbatchDetail.getFinance().getRegistry());
			detail.setDebit(fbatchDetail.getAmount());
		}
		detail.setAccount(registryAccount);
		detail.setConcept(obtainConcept(fbatchDetail.getFinance().getInvoice(), fbatchDetail.getAmount(), fbatchDetail.getFinanceBatch()));
		detail.setBalancingAccount(bankAccount);

		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	private void insertFBatchDetailLastEntryDetail(Account bankAccount, AccountEntry entry, String concept, double amount) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(bankAccount);
		detail.setConcept(concept);
		detail.setBalancingAccount(null);
		if (entry.getType().equals(AccountEntryType.COLLECTION)) {
			detail.setDebit(amount);
		} else{
			detail.setCredit(amount);
		}

		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	public AccountEntry recordFinance(Finance finance, RegistryBank registryBank, Date paymentDate) throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setType((finance.isPayment())?AccountEntryType.PAYMENT:AccountEntryType.COLLECTION);
		recordingTo.setDate(paymentDate);
		recordingTo.setRegistryBank(registryBank);
		recordingTo.setSecurityLevel((finance.getSecurityLevel()==null)?SecurityLevel.OFFICIAL:finance.getSecurityLevel());
		recordingTo.setFinanceList(list);
		return recordFinances(recordingTo, null);
	}

	public AccountEntry recordFinances(FinanceRecordingTo to, AccountEntry entry) throws ManagerBeanException {
		Account bankAccount = (to.getRegistryBank()!= null)?AccountUtil.obtainRBankAccount(to.getRegistryBank()):AccountUtil.obtainCashAccount();
		if (entry == null) {
			entry = createAccountEntry(to);
		}
		insertFinanceEntryDetails(bankAccount, entry, to);
		return entry;
	}

	private void insertFinanceEntryDetails(Account bankAccount, AccountEntry entry, FinanceRecordingTo recordingTo) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Invoice invoice = null;
		Account registryAccount = null;
		double balancingAmount = 0;
		// Primer Apunte
		Iterator<Finance> iterator = recordingTo.getFinanceList().iterator();
		while (iterator.hasNext()) {
			Finance finance = iterator.next();
			invoice = finance.getInvoice();
			balancingAmount += finance.getTotalAmount();

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(entry);
			if (finance.getInvoice().getType().equals(InvoiceType.SALES)) {
				registryAccount = AccountUtil.obtainCustomerAccount(finance.getRegistry());
				detail.setCredit(finance.getTotalAmount());
			} else if(finance.getInvoice().getType().equals(InvoiceType.PURCHASE)) {
				registryAccount = AccountUtil.obtainSupplierAccount(finance.getRegistry());
				detail.setDebit(finance.getTotalAmount());
			} else {
				registryAccount = AccountUtil.obtainCreditorAccount(finance.getRegistry());
				detail.setDebit(finance.getTotalAmount());
			}
			detail.setAccount(registryAccount);
			detail.setConcept(obtainConcept(finance.getInvoice(), finance.getTotalAmount(), null));
			detail.setBalancingAccount(bankAccount);
			accountEntryDetailBean.insert(detail);
		}

		balancingAmount = CommonUtil.round(balancingAmount);
		// Segundo Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(bankAccount);
		detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept()))?obtainConcept(invoice, balancingAmount, null):recordingTo.getBalancingConcept());
		detail.setBalancingAccount((recordingTo.getFinanceList().size()==1)?registryAccount:null);
		if (entry.getType().equals(AccountEntryType.COLLECTION)) {
			detail.setDebit(balancingAmount);
		} else{
			detail.setCredit(balancingAmount);
		}
		accountEntryDetailBean.insert(detail);
	}

	public AccountEntry returnFinance(Finance finance, RegistryBank registryBank, Date returnDate) throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setType((finance.isPayment()?AccountEntryType.RETURNED_PAYMENT:AccountEntryType.RETURNED_COLLECTION));
		recordingTo.setDate(returnDate);
		recordingTo.setRegistryBank(registryBank);
		recordingTo.setSecurityLevel(finance.getSecurityLevel()==null?SecurityLevel.OFFICIAL:finance.getSecurityLevel());
		recordingTo.setFinanceList(list);
		return returnFinances(recordingTo);
	}

	private AccountEntry returnFinances(FinanceRecordingTo to) throws ManagerBeanException {
		AccountEntry entry = createAccountEntry(to);
		Account bankAccount = (to.getRegistryBank() != null) ? AccountUtil.obtainRBankAccount(to.getRegistryBank()) : AccountUtil.obtainCashAccount();
		if (to.getFinanceList().size() > 0) {
			Finance finance = to.getFinanceList().get(0);
			insertReturnEntryDetails(bankAccount, entry, finance);
		}
		return entry;
	}

	private void insertReturnEntryDetails(Account bankAccount, AccountEntry entry, Finance finance) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		// Primer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		Account registryAccount = null;
		if (finance.getInvoice().getType().equals(InvoiceType.SALES)) {
			registryAccount = AccountUtil.obtainCustomerAccount(finance.getRegistry());
			detail.setDebit(finance.getTotalAmount());
		} else if(finance.getInvoice().getType().equals(InvoiceType.PURCHASE)) {
			registryAccount = AccountUtil.obtainSupplierAccount(finance.getRegistry());
			detail.setCredit(finance.getTotalAmount());
		} else {
			registryAccount = AccountUtil.obtainCreditorAccount(finance.getRegistry());
			detail.setCredit(finance.getTotalAmount());
		}
		detail.setAccount(registryAccount);
		detail.setConcept(obtainReturnConcept(finance.getInvoice()));
		detail.setBalancingAccount(bankAccount);
		accountEntryDetailBean.insert(detail);

		// Segundo Apunte
		detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(bankAccount);
		detail.setConcept(obtainReturnConcept(finance.getInvoice()));
		detail.setBalancingAccount(registryAccount);
		if (entry.getType().equals(AccountEntryType.RETURNED_COLLECTION)) {
			detail.setCredit(finance.getTotalAmount());
		} else {
			detail.setDebit(finance.getTotalAmount());
		}
		accountEntryDetailBean.insert(detail);
	}

	private AccountEntry createAccountEntry(FinanceRecordingTo to) throws ManagerBeanException {
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(AccountUtil.obtainPeriod(to.getDate()).getId());
		entry.setEntryDate(to.getDate());
		entry.setType(to.getType());
		entry.setJournal(null);
		entry.setSecurityLevel(to.getSecurityLevel());

		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		return (AccountEntry)accountEntryBean.insert(entry);
	}

	private String obtainConcept(Invoice invoice, double total, FinanceBatch fbatch) {
		String concept = ((total < 0) ? A_FRA : (invoice.getType().equals(InvoiceType.SALES) ? C_FRA : P_FRA)) + invoice.getReferenceCode();
		String fbatchConcept = (fbatch != null) ? (" (R:" + fbatch.getId() + ")") : "";
		return StringUtils.abbreviate(concept + fbatchConcept, 32);
	}

	private String obtainReturnConcept(Invoice invoice) {
		return StringUtils.abbreviate(D_FRA + invoice.getReferenceCode(), 32);
	}

	public AccountEntryFinanceTracking insertAccountEntryFinanceTracking(AccountEntry entry, FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean accountEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		AccountEntryFinanceTracking accEntryTracking = new AccountEntryFinanceTracking();
		accEntryTracking.setAccountEntry(entry);
		accEntryTracking.setFinanceTracking(tracking);
		return (AccountEntryFinanceTracking) accountEntryFinanceTrackingBean.insert(accEntryTracking);
	}

	@SuppressWarnings("unchecked")
	public void removeAccountEntryFinanceTracking(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean accountEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFinanceTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_ID), tracking.getId());
		Iterator iterator = accountEntryFinanceTrackingBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			AccountEntryFinanceTracking accountEntryFinanceTracking = (AccountEntryFinanceTracking)iterator.next();
			accountEntryFinanceTrackingBean.remove(accountEntryFinanceTracking);
			removeAccountEntryDetails(accountEntryFinanceTracking.getAccountEntry());
			removeAccountEntry(accountEntryFinanceTracking.getAccountEntry());
		}
	}

	@SuppressWarnings("unchecked")
	private void removeAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
		Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			AccountEntryDetail accEntryDetail = (AccountEntryDetail) iter.next();
			accountEntryDetailBean.remove(accEntryDetail);
		}
	}

	private void removeAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
	}

}