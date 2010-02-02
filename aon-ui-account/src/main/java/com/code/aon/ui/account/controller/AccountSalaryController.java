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
import com.code.aon.account.AccountSalaryHeader;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.Period;
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
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class AccountSalaryController {

	private static final Logger LOGGER = Logger.getLogger(AccountExpensesController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private AccountSalaryHeader header;


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

	public AccountSalaryHeader getHeader() {
		return header;
	}

	public void setHeader(AccountSalaryHeader header) {
		this.header = header;
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
		this.header = initializeHeader();
	}
	
	private AccountSalaryHeader initializeHeader() {
		AccountSalaryHeader header = new AccountSalaryHeader();
		header.setRBank(new RegistryBank());
		header.getRBank().setBank(new Bank());
		header.setPeriod(new Period());
		header.setDate(new Date());
		return header;
	}
	
	@SuppressWarnings("unused")
	public void accept(ActionEvent event) throws ManagerBeanException {
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryDetails(getAccountEntry());
			entry = this.getAccountEntry();
		}
		entry.setEntryDate(getHeader().getDate());
		entry.setAccountPeriod(getHeader().getPeriod().getId());
		entry.setJournal(null);
		entry.setType(AccountEntryType.SALARY);
		entry = insertorUpdateAccountEntry(entry);
		insertEntryDetails(entry);
		setAccountEntry(entry);
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	@SuppressWarnings("unused")
	public void onRemove(ActionEvent event){
		deleteAccountEntryDetails(getAccountEntry());
		deleteAccountEntry(getAccountEntry());
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
			Account salaryAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.SALARY_ACCOUNT);
			detail.setAccount(salaryAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getDescription());
			detail.setDebit(getHeader().getGrossSalary());
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(salaryAccount);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(getHeader().getRetention());
			accountEntryDetailBean.insert(detail);
			// Tercer Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(salaryAccount);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(getHeader().getSocialInsurance());
			accountEntryDetailBean.insert(detail);
			// Cuarto Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(AccountUtil.obtainRBankAccount(getHeader().getRBank()));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(salaryAccount);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(getHeader().getNetSalary());
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
	public void onRBankChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), event.getNewValue());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.header.setRBank((RegistryBank)iter.next());
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