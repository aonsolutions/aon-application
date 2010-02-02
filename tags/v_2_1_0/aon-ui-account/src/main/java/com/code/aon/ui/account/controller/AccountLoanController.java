package com.code.aon.ui.account.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.Loan;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Bank;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.account.utils.AccountPeriodValidator;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class AccountLoanController {

	private static final Logger LOGGER = Logger.getLogger(AccountLoanController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private Loan loan;


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

	@SuppressWarnings("unused")
	public void onReset(MenuEvent event){
		reset();
	}
	
	@SuppressWarnings("unused")
	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.loan = initializeLoan();
	}
	
	private Loan initializeLoan() {
		Loan loan = new Loan();
		loan.setRegistryBank(new RegistryBank());
		loan.getRegistryBank().setBank(new Bank());
		loan.setLoanDate(new Date());
		return loan;
	}
	
	@SuppressWarnings("unused")
	public void accept(ActionEvent event) throws ManagerBeanException {
		AccountPeriodValidator.validateAccountPeriod(getLoan().getLoanDate());
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryDetails(getAccountEntry());
			entry = this.getAccountEntry();
		}
		Loan loan = insertLoan(getLoan());
		entry.setEntryDate(getLoan().getLoanDate());
		entry.setAccountPeriod(AccountUtil.obtainPeriod(getLoan().getLoanDate()).getId());
		entry.setJournal(null);
		entry.setType(AccountEntryType.LOAN);
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

	@SuppressWarnings("unused")
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
			detail.setBalancingAccount(rBankAccount);
			detail.setConcept(getLoan().getDescription());
			detail.setCredit(getLoan().getAmount());
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			Account account270 = AccountUtil.obtainAccount("270");
			detail.setAccount(account270);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(loanAccount);
			detail.setConcept(getLoan().getDescription());
			detail.setDebit(getLoan().getExpenses());
			accountEntryDetailBean.insert(detail);
			// Tercer Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(rBankAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(loanAccount);
			detail.setConcept(getLoan().getDescription());
			detail.setDebit(getLoan().getAmount() - getLoan().getExpenses());
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
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
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

	@SuppressWarnings("unchecked")
	public void onRBankChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), event.getNewValue());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.getLoan().setRegistryBank((RegistryBank)iter.next());
			}
		}
	}
	
	private void loadAccountEntryController(AccountEntry entry) {
		try {
			AccountEntryController entryController = (AccountEntryController)AonUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountEntryController", e);
		}
	}
}
