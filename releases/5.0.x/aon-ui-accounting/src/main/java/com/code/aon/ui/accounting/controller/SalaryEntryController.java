package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.SalaryEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class SalaryEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	private AccountEntry accountEntry;
	private SalaryEntryHeader header;
	private String navigationKey;
	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;

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

	public SalaryEntryHeader getHeader() {
		return header;
	}

	public void setHeader(SalaryEntryHeader header) {
		this.header = header;
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
	
	private SalaryEntryHeader initializeHeader() throws ManagerBeanException {
		SalaryEntryHeader header = new SalaryEntryHeader();
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
				entry.setType(AccountEntryType.SALARY);
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

	private void insertEntryDetails(AccountEntry entry) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccount(getAccountingUtil().obtainDefaultAccount(DefaultAccounts.SALARY_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getConcept());
			detail.setDebit(getHeader().getGrossSalary());
			accountEntryDetailBean.insert(detail);
			
			if (getHeader().getAllowance() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(getAccountingUtil().obtainDefaultAccount(DefaultAccounts.ALLOWANCE_ACCOUNT));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getHeader().getConcept());
				detail.setDebit(getHeader().getAllowance());
				accountEntryDetailBean.insert(detail);
			}

			if (getHeader().getAllowance() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(getAccountingUtil().obtainDefaultAccount(DefaultAccounts.COMPENSATION_ACCOUNT));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getHeader().getConcept());
				detail.setDebit(getHeader().getCompensation());
				accountEntryDetailBean.insert(detail);
			}
			
			if (getHeader().getCompanySocialInsurance() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(getAccountingUtil().obtainDefaultAccount(DefaultAccounts.COMPANY_SOCIAL_INSURANCE_ACCOUNT));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getHeader().getConcept());
				detail.setDebit(getHeader().getCompanySocialInsurance());
				accountEntryDetailBean.insert(detail);
			}

			if (getHeader().getRetention() != 0) {
				detail = new AccountEntryDetail();
				detail.setAccount(getAccountingUtil().obtainDefaultAccount(DefaultAccounts.SALARY_CHARGED_RETENTION_ACCOUNT));
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getHeader().getConcept());
				detail.setCredit(getHeader().getRetention());
				accountEntryDetailBean.insert(detail);
			}

			detail = new AccountEntryDetail();
			detail.setAccount(getAccountingUtil().obtainDefaultAccount(DefaultAccounts.SOCIAL_INSURANCE_ACCOUNT));
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getConcept());
			detail.setCredit(getHeader().getTotalSocialInsurance());
			accountEntryDetailBean.insert(detail);

			// Quinto Apunte
			detail = new AccountEntryDetail();
			if (getHeader().getRegistryBank() != null && getHeader().getRegistryBank().getId() != null) {
				detail.setAccount(getAccountBridgeUtil().obtainRBankAccount(getHeader().getRegistryBank()));
			} else {
				detail.setAccount(getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PENDING_SALARY_ACCOUNT));
			}
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getHeader().getConcept());
			detail.setCredit(getHeader().getNetSalary());
			accountEntryDetailBean.insert(detail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error inserting details for AccountEntry with id = " + entry.getId(), e);
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
			LOGGER.error("Error deleting details related with AccountEntry with id=" + accountEntry.getId(), e);
		}
	}

	private void deleteAccountEntry(AccountEntry accountEntry) {
		try {
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			accountEntryBean.remove(accountEntry);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error deleting AccountEntry with id= " + accountEntry.getId(), e);
		}
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
			LOGGER.error("Error loading AccountEntryController", e);
		}
	}

	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		setAccountEntry(entry);

		SalaryEntryHeader header = new SalaryEntryHeader();
		header.setPeriod(new Period());
		header.getPeriod().setId(entry.getAccountPeriod());
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "465*");
		if (accountEntryDetail != null) {
			header.setConcept(accountEntryDetail.getConcept());
		} else {
			accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
			if (accountEntryDetail != null) {
				header.setRegistryBank(getAccountBridgeUtil().obtainRBank(accountEntryDetail.getAccount().getId()));
				header.setConcept(accountEntryDetail.getConcept());
			}
		}
		header.setSecurityLevel(entry.getSecurityLevel());
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "640*");
		header.setGrossSalary((accountEntryDetail != null)?accountEntryDetail.getDebit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "641*");
		header.setCompensation((accountEntryDetail != null)?accountEntryDetail.getDebit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "629*");
		header.setAllowance((accountEntryDetail != null)?accountEntryDetail.getDebit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "475*");
		header.setRetention((accountEntryDetail != null)?accountEntryDetail.getCredit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "642*");
		header.setCompanySocialInsurance((accountEntryDetail != null)?accountEntryDetail.getDebit():0);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "476*");
		header.setEmployeeSocialInsurance1((accountEntryDetail != null)?accountEntryDetail.getCredit() - header.getCompanySocialInsurance():0);
		header.setEmployeeSocialInsurance2(0);
		header.setEmployeeSocialInsurance3(0);
		header.setEmployeeSocialInsurance4(0);
		setHeader(header);
	}

	@Override
	public String getNavigationKey() {
		return "account_salary_entry";
	}
	
}
