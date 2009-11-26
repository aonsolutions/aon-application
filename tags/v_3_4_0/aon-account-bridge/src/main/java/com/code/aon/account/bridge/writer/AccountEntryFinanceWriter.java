package com.code.aon.account.bridge.writer;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.registry.RegistryBank;

/**
 * The Class AccountEntryFinanceWriter.
 */
public class AccountEntryFinanceWriter {

	@SuppressWarnings("unchecked")
	public AccountEntry recordFinances(FinanceRecordingTo to) throws ManagerBeanException{
		AccountEntry entry = createAccountEntry(to);
		Account bankAccount = (to.getRegistryBank() != null?AccountUtil.obtainRBankAccount(to.getRegistryBank()):AccountUtil.obtainCashAccount());
		Iterator iter = to.getFinanceList().iterator();
		double amount = 0.0;
		while(iter.hasNext()){
			Finance finance = (Finance)iter.next();
			amount += finance.getTotalAmount();
			insertEntryDetail(bankAccount,finance, entry);
		}
		if(to.getFinanceList().size() > 0){
			Finance finance = to.getFinanceList().get(0);
			insertLastEntryDetail(bankAccount, entry, finance, amount);
		}else{
			insertLastEntryDetail(bankAccount, entry, null, amount);
		}
		return entry;
	}

	public AccountEntry recordFinance(Finance finance, RegistryBank registryBank, Date paymentDate) throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setRegistryBank(registryBank);
		recordingTo.setFinanceList(list);
		recordingTo.setDate(paymentDate);
		recordingTo.setType((finance.isPayment()?AccountEntryType.PAYMENT:AccountEntryType.COLLECTION));
		recordingTo.setSecurityLevel(finance.getSecurityLevel()==null?SecurityLevel.OFFICIAL:finance.getSecurityLevel());
		return recordFinances(recordingTo);
	}

	public AccountEntry returnFinances(FinanceRecordingTo to) throws ManagerBeanException {
		AccountEntry entry = createAccountEntry(to);
		Account bankAccount = (to.getRegistryBank() != null?AccountUtil.obtainRBankAccount(to.getRegistryBank()):AccountUtil.obtainCashAccount());
		if(to.getFinanceList().size() > 0){
			Finance finance = to.getFinanceList().get(0);
			insertReturnEntryDetails(bankAccount, entry, finance);
		}
		return entry;
	}

	public AccountEntry returnFinance(Finance finance, RegistryBank registryBank, Date returnDate) throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setRegistryBank(registryBank);
		recordingTo.setFinanceList(list);
		recordingTo.setDate(returnDate);
		recordingTo.setType((finance.isPayment()?AccountEntryType.RETURNED_PAYMENT:AccountEntryType.RETURNED_COLLECTION));
		recordingTo.setSecurityLevel(finance.getSecurityLevel()==null?SecurityLevel.OFFICIAL:finance.getSecurityLevel());
		return returnFinances(recordingTo);
	}

	private AccountEntry createAccountEntry(FinanceRecordingTo to) throws ManagerBeanException {
		try {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			AccountEntry entry = new AccountEntry();
			entry.setEntryDate(to.getDate());
			entry.setJournal(null);
			entry.setType(to.getType());
			entry.setAccountPeriod(AccountUtil.obtainPeriod(to.getDate()).getId());
			entry.setSecurityLevel(to.getSecurityLevel());
			entry = (AccountEntry) accountEntryBean.insert(entry);
			return entry;
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error creating AccountEntry", e);
		}
	}

	
	private void insertEntryDetail(Account bankAccount, Finance finance, AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		if(finance.getInvoice().getType().equals(InvoiceType.SALES)){
			detail.setAccount(AccountUtil.obtainCustomerAccount(finance.getRegistry()));
			detail.setCredit(finance.getTotalAmount());
		}else if(finance.getInvoice().getType().equals(InvoiceType.PURCHASE)){
			detail.setAccount(AccountUtil.obtainSupplierAccount(finance.getRegistry()));
			detail.setDebit(finance.getTotalAmount());
		}else{
			detail.setAccount(AccountUtil.obtainCreditorAccount(finance.getRegistry()));
			detail.setDebit(finance.getTotalAmount());
		}
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(bankAccount);
		detail.setConcept("N/Fra: " + finance.getInvoice().getSeries() + "/" + finance.getInvoice().getNumber());
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	private void insertLastEntryDetail(Account bankAccount, AccountEntry entry, Finance finance, double amount) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		if(entry.getType().equals(AccountEntryType.COLLECTION)){
			detail.setDebit(amount);
		}else{
			detail.setCredit(amount);
		}
		detail.setAccountEntry(entry);
		detail.setAccount(bankAccount);
		if(finance != null){
			detail.setConcept("N/Fra: " + finance.getInvoice().getSeries() + "/" + finance.getInvoice().getNumber());
		}else{
			detail.setConcept("");
		}
		detail.setBalancingAccount(null);
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	private void insertReturnEntryDetails(Account bankAccount, AccountEntry entry, Finance finance) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		// Primer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		Account registryAccount = null;
		if(finance.getInvoice().getType().equals(InvoiceType.SALES)){
			registryAccount = AccountUtil.obtainCustomerAccount(finance.getRegistry());
			detail.setDebit(finance.getTotalAmount());
		}else if(finance.getInvoice().getType().equals(InvoiceType.PURCHASE)){
			registryAccount = AccountUtil.obtainSupplierAccount(finance.getRegistry());
			detail.setCredit(finance.getTotalAmount());
		}else{
			registryAccount = AccountUtil.obtainCreditorAccount(finance.getRegistry());
			detail.setCredit(finance.getTotalAmount());
		}
		detail.setAccount(registryAccount);
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(bankAccount);
		detail.setConcept("N/Fra: " + finance.getInvoice().getSeries() + "/" + finance.getInvoice().getNumber());
		accountEntryDetailBean.insert(detail);
		// Segundo Apunte
		detail = new AccountEntryDetail();
		if(entry.getType().equals(AccountEntryType.RETURNED_COLLECTION)){
			detail.setCredit(finance.getTotalAmount());
		}else{
			detail.setDebit(finance.getTotalAmount());
		}
		detail.setAccountEntry(entry);
		detail.setAccount(bankAccount);
		detail.setConcept("N/Fra: " + finance.getInvoice().getSeries() + "/" + finance.getInvoice().getNumber());
		detail.setBalancingAccount(registryAccount);
		accountEntryDetailBean.insert(detail);
	}

	public AccountEntryFinanceTracking insertAccountEntryFinanceTracking(AccountEntry entry, FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean accountEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		AccountEntryFinanceTracking accEntryTracking = new AccountEntryFinanceTracking();
		accEntryTracking.setAccountEntry(entry);
		accEntryTracking.setFinanceTracking(tracking);
		return (AccountEntryFinanceTracking) accountEntryFinanceTrackingBean.insert(accEntryTracking);
	}

}