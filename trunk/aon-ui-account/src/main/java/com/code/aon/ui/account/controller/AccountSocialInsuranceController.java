package com.code.aon.ui.account.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.AccountSocialInsuranceHeader;
import com.code.aon.account.AccountSummary;
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

public class AccountSocialInsuranceController {

private static final Logger LOGGER = Logger.getLogger(AccountExpensesController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private AccountSocialInsuranceHeader header;


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

	public AccountSocialInsuranceHeader getHeader() {
		return header;
	}

	public void setHeader(AccountSocialInsuranceHeader header) {
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
	
	private AccountSocialInsuranceHeader initializeHeader() {
		AccountSocialInsuranceHeader header = new AccountSocialInsuranceHeader();
		header.setRegistryBank(new RegistryBank());
		header.getRegistryBank().setBank(new Bank());
		header.setDate(new Date());
		header.setDescription("");
		header.setAmount(0.0);
		header.setPeriod(new Period());
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
		entry.setType(AccountEntryType.SOCIAL_INSURANCE);
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
			Account socialInsuranceAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT);
			Account rBAccount = AccountUtil.obtainRBankAccount(getHeader().getRegistryBank()); 
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(rBAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(socialInsuranceAccount);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(getHeader().getAmount());
			detail.setDebit(0.0);
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(socialInsuranceAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(rBAccount);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(0.0);
			detail.setDebit(getHeader().getAmount());
			accountEntryDetailBean.insert(detail);
			// Tercer Apunte
			detail = new AccountEntryDetail();
			double dif = obtainSalaryDiference(socialInsuranceAccount, entry, getHeader().getAmount());
			detail.setAccount(socialInsuranceAccount);
			detail.setAccountEntry(entry);
			Account account642 = AccountUtil.obtainAccount("642");
			detail.setBalancingAccount(account642);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(dif);
			detail.setDebit(0.0);
			accountEntryDetailBean.insert(detail);
			// Cuarto Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(account642);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(socialInsuranceAccount);
			detail.setConcept(getHeader().getDescription());
			detail.setCredit(0.0);
			detail.setDebit(dif);
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting details for AccountEntry with id = " + entry.getId(), e);
		}
		
	}
	
	private double obtainSalaryDiference(Account account, AccountEntry entry, double amount) throws ManagerBeanException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(entry.getEntryDate());
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Integer month = new Integer(calendar.get(Calendar.MONTH) + 1);
		IManagerBean accountSummaryBean = BeanManager.getManagerBean(AccountSummary.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountAlias.ACCOUNT_SUMMARY_ACCOUNT_PERIOD), entry.getAccountPeriod());
		criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountAlias.ACCOUNT_SUMMARY_ACCOUNT_ID), account.getId());
		criteria.addEqualExpression(accountSummaryBean.getFieldName(IAccountAlias.ACCOUNT_SUMMARY_ENTRY_MONTH), month);
		Iterator iter = accountSummaryBean.getList(criteria).iterator();
		if(iter.hasNext()){
			AccountSummary accSum = (AccountSummary)iter.next();
			return (amount - accSum.getCredit());
		}
		return 0;
	}

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
	
	public void onRBankChange(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_ID), event.getNewValue());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.header.setRegistryBank((RegistryBank)iter.next());
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
