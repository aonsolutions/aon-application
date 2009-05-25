package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountUtils;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.utils.AccountPeriodValidator;
import com.code.aon.ui.form.FormUtil;

public class LoanEntryController implements ISpecialAccountEntry {

	private static final Logger LOGGER = Logger.getLogger(LoanEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private Loan loan;

	private AccountUtils accountUtils;

	public AccountUtils getAccountUtils() {
		if (accountUtils == null) {
			accountUtils = new AccountUtils();
		}
		return accountUtils;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	
	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.loan = initializeLoan();
	}
	
	private Loan initializeLoan() {
		Loan loan = new Loan();
		loan.setLoanDate(new Date());
		return loan;
	}
	
	public void accept(ActionEvent event) throws ManagerBeanException {
		AccountPeriodValidator.validateAccountPeriod(getLoan().getLoanDate());
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryDetails(getAccountEntry());
			entry = this.getAccountEntry();
		}
		insertLoan(getLoan());
		entry.setEntryDate(getLoan().getLoanDate());
		entry.setAccountPeriod(AccountUtil.obtainPeriod(getLoan().getLoanDate()).getId());
		entry.setJournal(null);
		entry.setType(AccountEntryType.LOAN);
		entry.setSecurityLevel(getLoan().getSecurityLevel());
		entry = insertorUpdateAccountEntry(entry);
		insertEntryDetails(entry);
		setAccountEntry(entry);
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	private Loan insertLoan(Loan loan) throws ManagerBeanException {
		IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
		return (Loan) loanBean.insert(loan);
	}

	public void onRemove(ActionEvent event) throws ManagerBeanException{
		try {
			deleteAccountEntryDetails(getAccountEntry());
			deleteAccountEntry(getAccountEntry());
			deleteLoanAccount(getLoan());
			deleteLoan(getLoan());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error Removing Loan", e);
		}
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
	
	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			Account loanAccount = AccountUtil.obtainLoanAccount(getLoan());
			Account rBankAccount = AccountUtil.obtainRBankAccount(getLoan().getRegistryBank());
			detail.setAccount(loanAccount);
			detail.setAccountEntry(entry);
			detail.setConcept(getLoan().getDescription());
			detail.setCredit(getLoan().getAmount());
			detail.setBalancingAccount(rBankAccount);
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(rBankAccount);
			detail.setAccountEntry(entry);
			detail.setConcept(getLoan().getDescription());
			detail.setDebit(getLoan().getAmount() - getLoan().getExpenses());
			detail.setBalancingAccount(loanAccount);
			accountEntryDetailBean.insert(detail);
			// Tercer Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.FINANCIAL_EXPENSES_ACCOUNT));;
			detail.setAccountEntry(entry);
			detail.setConcept(getLoan().getDescription());
			detail.setDebit(getLoan().getExpenses());
			detail.setBalancingAccount(loanAccount);
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting details for AccountEntry with id = " + entry.getId(), e);
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
			LOGGER.log(Level.SEVERE, "Error deleting details related with AccountEntry with id=" + accountEntry.getId(), e);
		}
	}

	private void deleteAccountEntry(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			accountEntryBean.remove(accountEntry);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting AccountEntry with id= " + accountEntry.getId(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void deleteLoanAccount(Loan loan) throws ManagerBeanException {
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			LoanAccount loanAccount  = (LoanAccount)iter.next();
			loanAccountBean.remove(loanAccount);
		}
	}

	private void deleteLoan(Loan loan) throws ManagerBeanException {
		IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
		loanBean.remove(loan);
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

	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		setAccountEntry(entry);
		Loan loan = obtainLoan(entry);
		setLoan(loan);
	}

	@SuppressWarnings("unchecked")
	private Loan obtainLoan(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = getAccountUtils().getEntryDetailFromAccountPattern(entry, AccountConstants.LOAN_ACCOUNT_PREFIX + "*");
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_ACCOUNT_ID), detail.getAccount().getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LoanAccount)iter.next()).getLoan();
		}
		return null;
	}

	@Override
	public String getNavigationKey() {
		return "account_loan_entry";
	}
}
