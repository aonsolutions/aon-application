package com.code.aon.ui.accounting.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.SocialInsuranceEntryHeader;
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
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class SocialInsuranceEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = Logger.getLogger(SocialInsuranceEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private SocialInsuranceEntryHeader header;

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

	public SocialInsuranceEntryHeader getHeader() {
		return header;
	}

	public void setHeader(SocialInsuranceEntryHeader header) {
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
	
	private SocialInsuranceEntryHeader initializeHeader() throws ManagerBeanException {
		SocialInsuranceEntryHeader header = new SocialInsuranceEntryHeader();
		header.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		header.setDate(new Date());
		header.setSecurityLevel(SecurityLevel.OFFICIAL);
		return header;
	}
	
	public String accept(){
		return navigationKey; 	
	}

	public void onAccept(ActionEvent event) {
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
				entry.setType(AccountEntryType.SOCIAL_INSURANCE);
				entry.setSecurityLevel(getHeader().getSecurityLevel());
				if (this.isNew) {
					entry = (AccountEntry)entryBean.insert(entry);
				} else {
					entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
					entry = (AccountEntry) entryBean.update(entry);
				}
				insertEntryDetails(entry);
				updateSalaryAccount(entry);
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
				deleteAccountEntryDetails(getAccountEntry());
				deleteAccountEntry(getAccountEntry());
				
				AccountEntryController entryController = (AccountEntryController)FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
				entryController.onEditSearch(null);
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
	
	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

			Account socialInsuranceAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT);
			Account bankAccount = null;
			if (header.getRegistryBank() != null && header.getRegistryBank().getId() != null) {
				bankAccount = AccountUtil.obtainRBankAccount(header.getRegistryBank());
			} else {
				bankAccount = AccountUtil.obtainCashAccount();
			}
			// Primer Apunte
			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(socialInsuranceAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(bankAccount);
			detail.setConcept(getHeader().getConcept());
			detail.setDebit(getHeader().getAmount());
			accountEntryDetailBean.insert(detail);
			// Segundo Apunte
			detail = new AccountEntryDetail();
			detail.setAccount(bankAccount);
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(socialInsuranceAccount);
			detail.setConcept(getHeader().getConcept());
			detail.setCredit(getHeader().getAmount());
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting details for AccountEntry with id = " + entry.getId(), e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateSalaryAccount(AccountEntry entry) throws Exception {
		Calendar currentFrom = new GregorianCalendar();
		currentFrom.setTime(entry.getEntryDate());
		currentFrom.set(Calendar.DATE, 1);
		Calendar currentTo = new GregorianCalendar();
		currentTo.setTime(entry.getEntryDate());
		currentTo.set(Calendar.DATE, 1);
		currentTo.add(Calendar.MONTH, 1);
		currentTo.add(Calendar.DATE, -1);

		Calendar previousFrom = new GregorianCalendar();
		previousFrom.setTime(entry.getEntryDate());
		previousFrom.set(Calendar.DATE, 1);
		previousFrom.add(Calendar.MONTH, -1);
		Calendar previousTo = new GregorianCalendar();
		previousTo.setTime(entry.getEntryDate());
		previousTo.set(Calendar.DATE, 1);
		previousTo.add(Calendar.DATE, -1);

		AccountEntry acumEntry = new AccountEntry();
		int acumEntryId = 0; 
		double acumAmount = 0;
		boolean found = false; 
		Account socialInsuranceAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT);
		Account companySocialInsuranceAccount = AccountUtil.obtainDefaultAccount(DefaultAccounts.COMPANY_SOCIAL_INSURANCE_ACCOUNT);

		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), currentFrom.getTime());
		criteria.addLessThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), currentTo.getTime());
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.SOCIAL_INSURANCE);
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), socialInsuranceAccount.getId());
		Iterator iterator = entryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			AccountEntryDetail entryDetail = (AccountEntryDetail)iterator.next();
			acumAmount += CommonUtil.round(entryDetail.getDebit() - entryDetail.getCredit());
		}

		criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), previousFrom.getTime());
		criteria.addLessThanOrEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), previousTo.getTime());
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_TYPE), AccountEntryType.SALARY);
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), socialInsuranceAccount.getId());
		iterator = entryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			AccountEntryDetail entryDetail = (AccountEntryDetail)iterator.next();
			acumAmount += CommonUtil.round(entryDetail.getDebit() - entryDetail.getCredit());
		}

		criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE), previousFrom.getTime());
		criteria.addLessThanOrEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE), previousTo.getTime());
		criteria.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE), AccountEntryType.SALARY);
		criteria.addOrder(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE));
		iterator = entryBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			acumEntry = (AccountEntry)iterator.next();
			acumEntryId = acumEntry.getId();
		} else {
			acumEntry.setEntryDate(previousTo.getTime());
			acumEntry.setAccountPeriod(AccountUtil.obtainPeriod(previousTo.getTime()).getId());
			acumEntry.setJournal(null);
			acumEntry.setType(AccountEntryType.SALARY);
			acumEntry.setSecurityLevel(getHeader().getSecurityLevel());
			acumEntry = (AccountEntry)entryBean.insert(acumEntry);
			acumEntryId = acumEntry.getId();
		}

		criteria = new Criteria();
		criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), acumEntryId);
		Expression expr1 = ExpressionUtilities.getEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), companySocialInsuranceAccount.getId());
		Expression expr2 = ExpressionUtilities.getEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), socialInsuranceAccount.getId());
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		iterator = entryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			AccountEntryDetail entryDetail = (AccountEntryDetail)iterator.next();
			if (entryDetail.getAccount().equals(companySocialInsuranceAccount)) {
				if (entryDetail.getCredit() > 0) {
					entryDetail.setCredit(CommonUtil.round(entryDetail.getCredit() + acumAmount));
				} else {
					entryDetail.setDebit(CommonUtil.round(entryDetail.getDebit() + acumAmount));
				}
			} else {
				if (entryDetail.getDebit() > 0) {
					entryDetail.setDebit(CommonUtil.round(entryDetail.getDebit() + acumAmount));
				} else {
					entryDetail.setCredit(CommonUtil.round(entryDetail.getCredit() + acumAmount));
				}
			}
			entryDetailBean.update(entryDetail);
			found = true;
		} 
		if (!found) {
			AccountEntryDetail entryDetail = new AccountEntryDetail();
			entryDetail.setAccount(companySocialInsuranceAccount);
			entryDetail.setAccountEntry(acumEntry);
			entryDetail.setBalancingAccount(socialInsuranceAccount);
			entryDetail.setConcept(getHeader().getConcept());
			entryDetail.setDebit(acumAmount);
			entryDetailBean.insert(entryDetail);

			entryDetail = new AccountEntryDetail();
			entryDetail.setAccount(socialInsuranceAccount);
			entryDetail.setAccountEntry(acumEntry);
			entryDetail.setBalancingAccount(companySocialInsuranceAccount);
			entryDetail.setConcept(getHeader().getConcept());
			entryDetail.setCredit(acumAmount);
			entryDetailBean.insert(entryDetail);
		}
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

		SocialInsuranceEntryHeader header = new SocialInsuranceEntryHeader();
		header.setPeriod(new Period());
		header.getPeriod().setId(entry.getAccountPeriod());
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "570*");
		if (accountEntryDetail == null) {
			accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
			header.setRegistryBank(AccountUtil.obtainRBank(accountEntryDetail.getAccount().getId()));
		}
		header.setConcept(accountEntryDetail.getConcept());
		header.setSecurityLevel(entry.getSecurityLevel());
		header.setAmount(accountEntryDetail.getCredit());
		setHeader(header);
	}

	@Override
	public String getNavigationKey() {
		return "account_social_insurance_entry";
	}
	
}
