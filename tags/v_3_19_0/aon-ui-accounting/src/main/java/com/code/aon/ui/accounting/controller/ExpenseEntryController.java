package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.ExpenseEntryHeader;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ExpenseEntryController implements ISpecialAccountEntry{
	
	private static final Logger LOGGER = Logger.getLogger(ExpenseEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;

	private ExpenseEntryHeader header;

	private AccountingUtil accountingUtil;

	private String navigationKey;

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
	
	public AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	public void onReset(ActionEvent event){
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void reset() throws ManagerBeanException {
		this.isNew = true;
		this.header = initializeHeader();
	}
	
	private ExpenseEntryHeader initializeHeader() throws ManagerBeanException {
		ExpenseEntryHeader header = new ExpenseEntryHeader();
		header.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		header.setDate(new Date());
		header.setAccount(null);
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		return header;
	}
	
	public String accept(){
		return navigationKey; 	
	}

	public void onAccept(ActionEvent event){
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
				this.navigationKey = "accountEntry_form";
				IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
				AccountEntry entry = new AccountEntry();
				if (!this.isNew) {
					deleteAccountEntryDetails(getAccountEntry());
					entry = this.getAccountEntry();
				}
				entry.setEntryDate(getHeader().getDate());
				entry.setAccountPeriod(getHeader().getPeriod().getId());
				entry.setType(AccountEntryType.EXPENSES);
				entry.setSecurityLevel(getHeader().getSecurityLevel());
				if (this.isNew) {
					entry = (AccountEntry)entryBean.insert(entry);
				} else {
					entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
					entry = (AccountEntry) entryBean.update(entry);
				}
				insertEntryDetails(entry);
				setAccountEntry(entry);
				
				this.isNew = false;
				loadAccountEntryController(entry);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				navigationKey = null;
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
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
	
	public void onRemove(ActionEvent event){
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
					
					AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
					entryController.onEditSearch(null);
				} catch (ManagerBeanException e) {
					String message = "Error deleting AccountEntry";
					LOGGER.log(Level.SEVERE, message , e);
					AonUtil.addErrorMessage(message);
				}
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage() ;
				LOGGER.log(Level.SEVERE, msg, e);
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
	
	private void insertEntryDetails(AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

		Account bankAccount = null;
		if (getHeader().getRegistryBank() != null && getHeader().getRegistryBank().getId() != null) {
			bankAccount = AccountUtil.obtainRBankAccount(getHeader().getRegistryBank());
		} else {
			bankAccount = AccountUtil.obtainCashAccount();
		}
		// Primer apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccount(getHeader().getAccount());
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(bankAccount);
		detail.setConcept(getHeader().getConcept());
		detail.setDebit(getHeader().getAmount());
		accountEntryDetailBean.insert(detail);
		// Segundo apunte
		detail = new AccountEntryDetail();
		detail.setAccount(bankAccount);
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(getHeader().getAccount());
		detail.setConcept(getHeader().getConcept());
		detail.setCredit(getHeader().getAmount());
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

		ExpenseEntryHeader header = new ExpenseEntryHeader();
		header.setPeriod(new Period());
		header.getPeriod().setId(entry.getAccountPeriod());
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
		if (accountEntryDetail != null) {
			header.setRegistryBank(AccountUtil.obtainRBank(accountEntryDetail.getAccount().getId()));
		}
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "6*");
		header.setAccount(accountEntryDetail.getAccount());
		header.setConcept(accountEntryDetail.getConcept());
		header.setSecurityLevel(entry.getSecurityLevel());
		header.setAmount( CommonUtil.round(accountEntryDetail.getDebit() - accountEntryDetail.getCredit()));
		setHeader(header);
	}

	@Override
	public String getNavigationKey() {
		return "account_expense_entry";
	}

}
