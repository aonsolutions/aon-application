package com.code.aon.account.bridge.writer;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;

public class AccountEntryFinanceWriter {

	private static final String C_FRA = "Cobro Fra: ";
	private static final String P_FRA = "Pago Fra: ";
	private static final String A_FRA = "Abono Fra: ";
	private static final String D_FRA = "Dev. Fra: ";
	private static final String ENTRY = "Apunte: ";
	
	private AccountBridgeUtil accountBridgeUtil;
	private AccountingUtil accountingUtil;

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

	@SuppressWarnings("unchecked")
	public AccountEntry recordFBatchDetails(FinanceRecordingTo to, FinanceBatch fbatch) throws ManagerBeanException {
		AccountEntry entry = createAccountEntry(to);

		Iterator iterator = to.getFBatchDetailList().iterator();
		double amount = 0.0;
		while (iterator.hasNext()) {
			FinanceBatchDetail fbatchDetail = (FinanceBatchDetail)iterator.next();
			amount += fbatchDetail.getAmount();
			insertFBatchDetailEntryDetail(to.getPaymentAccount(), fbatchDetail, entry);
		}
		insertFBatchDetailLastEntryDetail(to.getPaymentAccount(), entry, fbatch.getDescription(), amount);
		return entry;
	}

	private void insertFBatchDetailEntryDetail(Account paymentAccount, FinanceBatchDetail fbatchDetail, AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		Account registryAccount = null;
		if (!fbatchDetail.getFinance().isPayment()) {
			registryAccount = getAccountBridgeUtil().obtainCustomerAccount(fbatchDetail.getFinance().getRegistry());
			detail.setCredit(fbatchDetail.getAmount());
		} else {
			registryAccount = getAccountBridgeUtil().obtainSupplierAccount(fbatchDetail.getFinance().getRegistry());
			if (registryAccount == null) {
				registryAccount = getAccountBridgeUtil().obtainCreditorAccount(fbatchDetail.getFinance().getRegistry());
			}
			detail.setDebit(fbatchDetail.getAmount());
		}
		detail.setAccount(registryAccount);
		detail.setConcept(obtainConcept(fbatchDetail.getFinance(), fbatchDetail.getAmount(), fbatchDetail.getFinanceBatch()));
		detail.setDocumentNumber(fbatchDetail.getFinance().getDocumentNumber());
		detail.setBalancingAccount(paymentAccount);

		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	private void insertFBatchDetailLastEntryDetail(Account paymentAccount, AccountEntry entry, String concept, double amount) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(paymentAccount);
		detail.setConcept(concept);
		detail.setDocumentNumber(null);
		detail.setBalancingAccount(null);
		if (entry.getType().equals(AccountEntryType.COLLECTION)) {
			detail.setDebit(amount);
		} else{
			detail.setCredit(amount);
		}

		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	public AccountEntry recordFinance(Finance finance, RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail, Date paymentDate) 
		throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setType((finance.isPayment()) ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
		recordingTo.setDate(paymentDate);
		recordingTo.setPaymentAccount(obtainPaymentAccount(registryBank, payMethodTypeDetail));
		recordingTo.setSecurityLevel((finance.getSecurityLevel()==null) ? SecurityLevel.OFFICIAL : finance.getSecurityLevel());
		recordingTo.setFinanceList(list);
		return recordFinances(recordingTo, null);
	}

	public AccountEntry recordFinances(FinanceRecordingTo to, AccountEntry entry) throws ManagerBeanException {
		if (entry == null) {
			entry = createAccountEntry(to);
		}
		insertFinanceEntryDetails(to.getPaymentAccount(), entry, to);
		return entry;
	}

	private void insertFinanceEntryDetails(Account paymentAccount, AccountEntry entry, FinanceRecordingTo recordingTo) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account registryAccount = null;
		String concept = null;
		String documentNumber = null;
		double balancingAmount = 0;
		// Primer Apunte
		for (Finance finance: recordingTo.getFinanceList()) {
			if (!finance.isPayment()) {
				registryAccount = getAccountBridgeUtil().obtainCustomerAccount(finance.getRegistry());
			} else {
				registryAccount = getAccountBridgeUtil().obtainSupplierAccount(finance.getRegistry());
				if (registryAccount == null) {
					registryAccount = getAccountBridgeUtil().obtainCreditorAccount(finance.getRegistry());
				}
			}
			concept = obtainConcept(finance, finance.getTotalAmount(), null);
			documentNumber = finance.getDocumentNumber();
			balancingAmount += finance.getTotalAmount();

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(entry);
			detail.setAccount(registryAccount);
			detail.setBalancingAccount(paymentAccount);
			detail.setConcept(concept);
			detail.setCredit((!finance.isPayment()) ? finance.getTotalAmount() : 0);
			detail.setDebit((!finance.isPayment()) ? 0 : finance.getTotalAmount());
			detail.setDocumentNumber(documentNumber);
			accountEntryDetailBean.insert(detail);
		}
		
		balancingAmount = CommonUtil.round(balancingAmount);
		// Segundo Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(paymentAccount);
		detail.setBalancingAccount((recordingTo.getFinanceList().size()==1) ? registryAccount : null);
		detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
		detail.setCredit((!entry.getType().equals(AccountEntryType.COLLECTION)) ? balancingAmount : 0);
		detail.setDebit((!entry.getType().equals(AccountEntryType.COLLECTION)) ? 0 : balancingAmount);
		detail.setDocumentNumber((recordingTo.getFinanceList().size()==1) ? documentNumber : null);
		accountEntryDetailBean.insert(detail);
	}

	public AccountEntry returnFinance(Finance finance, RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail, Date returnDate) 
		throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setType((finance.isPayment()) ? AccountEntryType.RETURNED_PAYMENT : AccountEntryType.RETURNED_COLLECTION);
		recordingTo.setDate(returnDate);
		recordingTo.setPaymentAccount(obtainPaymentAccount(registryBank, payMethodTypeDetail));
		recordingTo.setSecurityLevel((finance.getSecurityLevel()==null) ? SecurityLevel.OFFICIAL : finance.getSecurityLevel());
		recordingTo.setFinanceList(list);
		return returnFinances(recordingTo);
	}

	private AccountEntry returnFinances(FinanceRecordingTo to) throws ManagerBeanException {
		AccountEntry entry = createAccountEntry(to);
		if (to.getFinanceList().size() > 0) {
			Finance finance = to.getFinanceList().get(0);
			insertReturnEntryDetails(to.getPaymentAccount(), entry, finance);
		}
		return entry;
	}

	private void insertReturnEntryDetails(Account paymentAccount, AccountEntry entry, Finance finance) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account registryAccount = null;
		if (!finance.isPayment()) {
			registryAccount = getAccountBridgeUtil().obtainCustomerAccount(finance.getRegistry());
		} else {
			registryAccount = getAccountBridgeUtil().obtainSupplierAccount(finance.getRegistry());
			if (registryAccount == null) {
				registryAccount = getAccountBridgeUtil().obtainCreditorAccount(finance.getRegistry());
			}
		}
		String concept = obtainReturnConcept(finance);
		String documentNumber = finance.getDocumentNumber();

		// Primer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(registryAccount);
		detail.setBalancingAccount(paymentAccount);
		detail.setConcept(concept);
		detail.setCredit((!finance.isPayment()) ? 0 : finance.getTotalAmount());
		detail.setDebit((!finance.isPayment()) ? finance.getTotalAmount() : 0);
		detail.setDocumentNumber(documentNumber);
		accountEntryDetailBean.insert(detail);

		// Segundo Apunte
		detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(paymentAccount);
		detail.setBalancingAccount(registryAccount);
		detail.setConcept(concept);
		detail.setCredit((!entry.getType().equals(AccountEntryType.COLLECTION)) ? 0 : finance.getTotalAmount());
		detail.setDebit((!entry.getType().equals(AccountEntryType.COLLECTION)) ? finance.getTotalAmount() : 0);
		detail.setDocumentNumber(documentNumber);
		accountEntryDetailBean.insert(detail);
	}

	private AccountEntry createAccountEntry(FinanceRecordingTo to) throws ManagerBeanException {
		Period period = to.getPeriod();
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod((period!=null&&period.getId()!=null) ? period.getId() : getAccountingUtil().obtainPeriod(to.getDate()).getId());
		entry.setEntryDate(to.getDate());
		entry.setType(to.getType());
		entry.setJournal(null);
		entry.setSecurityLevel(to.getSecurityLevel());

		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		return (AccountEntry)accountEntryBean.insert(entry);
	}

	private Account obtainPaymentAccount(RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail) throws ManagerBeanException {
		Account account = null;
		if (registryBank != null) {
			account = getAccountBridgeUtil().obtainRBankAccount(registryBank);
		} else if (payMethodTypeDetail != null) {
			account = getAccountBridgeUtil().obtainPayMethodTypeDetailAccount(payMethodTypeDetail);
		}
		return (account!=null) ? account : getAccountingUtil().obtainCashAccount();
	}

	private String obtainConcept(Finance finance, double total, FinanceBatch fbatch) {
		String prefix = (total < 0) ? A_FRA : (!finance.isPayment()) ? C_FRA : P_FRA;
		String concept = (!finance.isEmptyInvoice()) ? finance.getInvoice().getReferenceCode() : finance.getConcept();
		String fbatchConcept = (fbatch != null) ? (" (R:" + fbatch.getId() + ")") : "";
		return StringUtils.abbreviate(prefix + concept + fbatchConcept, 32);
	}

	private String obtainReturnConcept(Finance finance) {
		String prefix = D_FRA;
		String concept = (!finance.isEmptyInvoice()) ? finance.getInvoice().getReferenceCode() : finance.getConcept();
		return StringUtils.abbreviate(prefix + concept, 32);
	}

	public AccountEntryFinanceTracking recordFinanceTracking(FinanceTracking tracking) throws ManagerBeanException {
		AccountEntry entry = null;
		AccountEntryFinanceTracking aeft = null;
		if (tracking.getType() == FinanceTrackingType.PAID) {
			entry = recordFinance(tracking.getFinance(), tracking.getRegistryBank(), tracking.getPayMethodTypeDetail(), tracking.getTrackingDate());
		} else if (tracking.getType() == FinanceTrackingType.RETURNED) {
			entry = returnFinance(tracking.getFinance(), tracking.getRegistryBank(), tracking.getPayMethodTypeDetail(), tracking.getTrackingDate());
		}
		if (entry != null) {
			tracking.setDescription(ENTRY + entry.getId());
			tracking.setRecorded(true);
			IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			trackingBean.update(tracking);
			aeft = insertAccountEntryFinanceTracking(entry, tracking);
		}
		return aeft;
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