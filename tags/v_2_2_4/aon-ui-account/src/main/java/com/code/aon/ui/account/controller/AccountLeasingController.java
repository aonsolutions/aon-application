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
import com.code.aon.account.Leasing;
import com.code.aon.account.bridge.LeasingAccount;
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
import com.code.aon.product.Tax;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.account.utils.AccountPeriodValidator;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class AccountLeasingController {

	private static final Logger LOGGER = Logger.getLogger(AccountLeasingController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private Leasing leasing;
	

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

	public Leasing getLeasing() {
		return leasing;
	}

	public void setLeasing(Leasing leasing) {
		this.leasing = leasing;
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
		this.leasing = initializeLeasing();
	}
	
	private Leasing initializeLeasing() {
		Leasing leasing = new Leasing();
		leasing.setRegistryBank(new RegistryBank());
		leasing.getRegistryBank().setBank(new Bank());
		leasing.setLeasingDate(new Date());
		leasing.setFixedAssetAccount(new Account());
		leasing.setVat(new Tax());
		return leasing;
	}
	
	@SuppressWarnings("unused")
	public void accept(ActionEvent event) throws ManagerBeanException {
		AccountPeriodValidator.validateAccountPeriod(getLeasing().getLeasingDate());
		AccountEntry entry = new AccountEntry();
		if(!this.isNew){
			deleteAccountEntryDetails(getAccountEntry());
			entry = this.getAccountEntry();
		}
		Leasing leasing = insertLeasing(getLeasing());
		entry.setEntryDate(getLeasing().getLeasingDate());
		entry.setAccountPeriod(AccountUtil.obtainPeriod(getLeasing().getLeasingDate()).getId());
		entry.setJournal(null);
		entry.setType(AccountEntryType.LEASING);
		entry.setSecurityLevel(getLeasing().getSecurityLevel());
		entry = insertorUpdateAccountEntry(entry);
		insertEntryDetails(entry);
		setAccountEntry(entry);
		this.isNew = false;
		loadAccountEntryController(entry);
	}
	
	private Leasing insertLeasing(Leasing leasing) throws ManagerBeanException {
		IManagerBean leasingBean = BeanManager.getManagerBean(Leasing.class);
		return (Leasing) leasingBean.insert(leasing);
	}

	@SuppressWarnings("unused")
	public void onRemove(ActionEvent event) throws ManagerBeanException{
		try {
			deleteAccountEntryDetails(getAccountEntry());
			deleteAccountEntry(getAccountEntry());
			deleteLeasingAccount(getLeasing());
			deleteLeasing(getLeasing());
		} catch (ManagerBeanException e) {
			throw new ManagerBeanException("Error removing Leasing",e);
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
			detail.setAccount(getLeasing().getFixedAssetAccount());
			detail.setAccountEntry(entry);
			detail.setConcept(getLeasing().getDescription());
			detail.setDebit(getLeasing().getAmount());
			Account leasingAccount = AccountUtil.obtainLeasingAccount(getLeasing());
			detail.setBalancingAccount(leasingAccount);
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(leasingAccount);
			detail.setAccountEntry(entry);
			detail.setConcept(getLeasing().getDescription());
			detail.setCredit(getLeasing().getAmount());
			detail.setBalancingAccount(getLeasing().getFixedAssetAccount());
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
	private void deleteLeasingAccount(Leasing leasing) throws ManagerBeanException {
		IManagerBean leasingAccountBean = BeanManager.getManagerBean(LeasingAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(leasingAccountBean.getFieldName(IAccountBridgeAlias.LEASING_ACCOUNT_LEASING_ID), leasing.getId());
		Iterator iter = leasingAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			LeasingAccount leasingAccount = (LeasingAccount)iter.next();
			leasingAccountBean.remove(leasingAccount);
		}
	}

	private void deleteLeasing(Leasing leasing) throws ManagerBeanException {
		IManagerBean leasingBean = BeanManager.getManagerBean(Leasing.class);
		leasingBean.remove(leasing);
	}
	
	@SuppressWarnings("unchecked")
	public void onRBankChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), event.getNewValue());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.getLeasing().setRegistryBank((RegistryBank)iter.next());
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