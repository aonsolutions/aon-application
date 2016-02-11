package com.code.aon.ui.accounting.controller.entry;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.ExpenseEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ExpenseEntryController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseEntryController.class.getName()); 

	private ExpenseEntry entry;
	private String navigationKey;
	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;
	private int payMethodTypeDetailsSize; 
	
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

	public ExpenseEntry getEntry() {
		return entry;
	}
	public void setHeader(ExpenseEntry entry) {
		this.entry = entry;
	}
	
	public void onReset(ActionEvent event){
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void reset() throws ManagerBeanException {
		this.entry = initializeentry();
		IManagerBean payMethodTypeDetailBean = BeanManager.getManagerBean(PayMethodTypeDetail.class);
		payMethodTypeDetailsSize = payMethodTypeDetailBean.getCount(null);
	}
	
	private ExpenseEntry initializeentry() throws ManagerBeanException {
		ExpenseEntry entry = new ExpenseEntry();
		entry.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		entry.setDate(new Date());
		entry.setAccount(null);
		entry.setSecurityLevel(SecurityLevel.OFFICIAL);
		return entry;
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
				entry.setEntryDate(getEntry().getDate());
				entry.setAccountPeriod(getEntry().getPeriod());
				entry.setType(AccountEntryType.EXPENSES);
				entry.setSecurityLevel(getEntry().getSecurityLevel());
				entry = (AccountEntry) entryBean.insert(entry);
				insertEntryDetails(entry);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				loadAccountEntryController(entry);
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
	
	private void insertEntryDetails(AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account balancingAccount = obtainPaymentAccount();
		// Primer apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccount(getEntry().getAccount());
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(balancingAccount);
		detail.setConcept(getEntry().getConcept());
		detail.setDebit(getEntry().getAmount());
		accountEntryDetailBean.insert(detail);
		// Segundo apunte
		detail = new AccountEntryDetail();
		detail.setAccount(balancingAccount);
		detail.setAccountEntry(entry);
		detail.setBalancingAccount(getEntry().getAccount());
		detail.setConcept(getEntry().getConcept());
		detail.setCredit(getEntry().getAmount());
		accountEntryDetailBean.insert(detail);
	}
	
	private Account obtainPaymentAccount() throws ManagerBeanException {
		Account account = null;
		if (getEntry().getDeposit() == 0 && getEntry().getRegistryBank() != null) {
			account = getAccountBridgeUtil().obtainRBankAccount(getEntry().getRegistryBank());
		} else if (getEntry().getDeposit() == 1 && getEntry().getPayMethodTypeDetail() != null) {
			account = getEntry().getPayMethodTypeDetail().getAccount();
		}
		return (account!=null) ? account : getAccountingUtil().obtainCashAccount();
	}

	
	private void loadAccountEntryController(AccountEntry entry) throws ManagerBeanException {
		AccountEntryController entryController = (AccountEntryController)FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), entry.getId());
		entryController.setCriteria(criteria);
		entryController.onSearch(null);
		entryController.getModel().setRowIndex(0);
		entryController.onSelect(null);
	}

	public int getPayMethodTypeDetailsSize() throws ManagerBeanException {
		return payMethodTypeDetailsSize;
	}

}
