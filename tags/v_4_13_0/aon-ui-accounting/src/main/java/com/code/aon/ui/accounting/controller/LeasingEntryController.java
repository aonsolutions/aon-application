package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Leasing;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.Bank;
import com.code.aon.config.Tax;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class LeasingEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = LoggerFactory.getLogger(LeasingEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private Leasing leasing;
	
	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;
	private AccountingPeriodUtil accountingPeriodUtil;
	
	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public AccountingPeriodUtil getAccountingPeriodUtil() {
		if (accountingPeriodUtil == null) {
			accountingPeriodUtil = new AccountingPeriodUtil();
		}
		return accountingPeriodUtil;
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

	public Leasing getLeasing() {
		return leasing;
	}

	public void setLeasing(Leasing leasing) {
		this.leasing = leasing;
	}

	
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
	
	public void accept(ActionEvent event) {
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				AccountingPeriodUtil.validateAccountPeriod(getLeasing().getLeasingDate());
				AccountEntry entry = new AccountEntry();
				if(!this.isNew){
					deleteAccountEntryDetails(getAccountEntry());
					entry = this.getAccountEntry();
				}
				insertLeasing(getLeasing());
				entry.setEntryDate(getLeasing().getLeasingDate());
				entry.setAccountPeriod(getAccountingUtil().obtainPeriod(getLeasing().getLeasingDate()).getId());
				entry.setJournal(null);
				entry.setType(AccountEntryType.LEASING);
				entry.setSecurityLevel(getLeasing().getSecurityLevel());
				entry = insertorUpdateAccountEntry(entry);
				insertEntryDetails(entry);
				setAccountEntry(entry);
				this.isNew = false;
				loadAccountEntryController(entry);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private Leasing insertLeasing(Leasing leasing) throws ManagerBeanException {
		IManagerBean leasingBean = BeanManager.getManagerBean(Leasing.class);
		return (Leasing) leasingBean.insert(leasing);
	}

	public void onRemove(ActionEvent event) {
		//inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				try {
					deleteAccountEntryDetails(getAccountEntry());
					deleteAccountEntry(getAccountEntry());
					deleteLeasingAccount(getLeasing());
					deleteLeasing(getLeasing());
				} catch (ManagerBeanException e) {
					throw new ManagerBeanException("Error removing Leasing",e);
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private AccountEntry insertorUpdateAccountEntry(AccountEntry entry) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		if(this.isNew){
			entry = (AccountEntry)entryBean.insert(entry);
		}else{
			entry = (AccountEntry)entryBean.update(entry);
		}
		return entry;
	}
	
	private void insertEntryDetails(AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		// Primer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccount(getLeasing().getFixedAssetAccount());
		detail.setAccountEntry(entry);
		detail.setConcept(getLeasing().getDescription());
		detail.setDebit(getLeasing().getAmount());
		Account leasingAccount = getAccountBridgeUtil().obtainLeasingAccount(getLeasing());
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
	}
	
	@SuppressWarnings("unchecked")
	private void deleteAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
		Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
		while(iter.hasNext()){
			accountEntryDetailBean.remove((AccountEntryDetail)iter.next());
		}
	}

	private void deleteAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
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
			criteria.addEqualExpression(rBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_ID), event.getNewValue());
			Iterator iter = rBankBean.getList(criteria).iterator();
			if(iter.hasNext()){
				this.getLeasing().setRegistryBank((RegistryBank)iter.next());
			}
		}
	}
	
	private void loadAccountEntryController(AccountEntry entry) throws ManagerBeanException {
		AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
		entryController.setCriteria(criteria);
		entryController.onSearch(null);
		entryController.getModel().setRowIndex(0);
		entryController.onSelect(null);
	}

	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		setAccountEntry(entry);
		Leasing leasing = obtainLeasing(entry);
		setLeasing(leasing);
	}

	@SuppressWarnings("unchecked")
	private Leasing obtainLeasing(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.LEASING_ACCOUNT_PREFIX + "*");
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LeasingAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LEASING_ACCOUNT_ACCOUNT_ID), detail.getAccount().getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LeasingAccount)iter.next()).getLeasing();
		}
		return null;
	}

	@Override
	public String getNavigationKey() {
		return "account_leasing_entry";
	}
	
	public String getPeriodMessage() {
		try {
			Period period = getAccountingUtil().getPeriod(getLeasing().getLeasingDate()); 
			return period.getId();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			return " - ";
		}
	}
}