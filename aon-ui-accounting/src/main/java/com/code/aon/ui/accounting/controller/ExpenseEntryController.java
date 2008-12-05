package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.ExpenseEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;

public class ExpenseEntryController {
	
	private static final Logger LOGGER = Logger.getLogger(ExpenseEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private ExpenseEntryHeader header;


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

	public ExpenseEntryHeader getHeader() {
		return header;
	}

	public void setHeader(ExpenseEntryHeader header) {
		this.header = header;
	}
	
	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.header = initializeHeader();
	}
	
	private ExpenseEntryHeader initializeHeader() {
		ExpenseEntryHeader header = new ExpenseEntryHeader();
		header.setDate(new Date());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		return header;
	}
	
	public void accept(ActionEvent event) throws ManagerBeanException {
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryDetails(getAccountEntry());
			entry = this.getAccountEntry();
		}
		entry.setAccountPeriod(getHeader().getPeriod().getId());
		entry.setEntryDate(getHeader().getDate());
		entry.setJournal(null);
		entry.setType(AccountEntryType.EXPENSES);
		entry.setSecurityLevel(getHeader().getSecurityLevel());
		entry = insertorUpdateAccountEntry(entry);
		insertEntryDetails(entry);
		setAccountEntry(entry);
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	public void onRemove(ActionEvent event){
		deleteAccountEntryDetails(getAccountEntry());
		deleteAccountEntry(getAccountEntry());
	}
	
	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			// Primer apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(getHeader().getAccount());
			detail.setConcept(getHeader().getDescription());
			detail.setAccountEntry(entry);
			detail.setDebit(getHeader().getAmount());
			Account bankAccount = AccountUtil.obtainRBankAccount(getHeader().getRBank());
			detail.setBalancingAccount(bankAccount);
			entryDetailBean.insert(detail);
			// Segundo apunte
			detail = new AccountEntryDetail();
			detail.setAccount(bankAccount);
			detail.setConcept(getHeader().getDescription());
			detail.setAccountEntry(entry);
			detail.setCredit(getHeader().getAmount());
			detail.setBalancingAccount(getHeader().getAccount());
			entryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting AccountEntryDetails related with Account entry with id= " + entry.getId(), e);
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
			LOGGER.log(Level.SEVERE, "Error deleting AccountEntryDetails related with AccountEntry with id= " + accountEntry.getId(), e);
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
}