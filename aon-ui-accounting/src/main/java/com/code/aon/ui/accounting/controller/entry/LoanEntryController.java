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
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.LoanStatus;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class LoanEntryController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(LoanEntryController.class.getName()); 
	
	private Period period;
	private Loan loan;
	private String navigationKey;
	private AccountBridgeUtil accountBridgeUtil;

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	public void onReset(ActionEvent event){
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void reset() throws ManagerBeanException {
		this.period = AccountingPeriodUtil.getDefaultPeriod();
		this.loan = initializeLoan();
	}
	
	private Loan initializeLoan() {
		Loan loan = new Loan();
		loan.setLoanDate(new Date());
		loan.setSecurityLevel(SecurityLevel.OFFICIAL);
		loan.setFeeAmount(0.0);
		loan.setStatus(LoanStatus.ACTIVE);
		return loan;
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
				entry.setEntryDate(getLoan().getLoanDate());
				entry.setAccountPeriod(getPeriod());
				entry.setType(AccountEntryType.LOAN);
				entry.setSecurityLevel(getLoan().getSecurityLevel());
				IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
				loanBean.insert(getLoan());
				entry = (AccountEntry)entryBean.insert(entry);
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
		// Primer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		Account loanAccount = getAccountBridgeUtil().obtainLoanAccount(getLoan());
		Account rBankAccount = getAccountBridgeUtil().obtainRBankAccount(getLoan().getRegistryBank());
		detail.setAccount(loanAccount);
		detail.setAccountEntry(entry);
		detail.setConcept(getLoan().getDescription());
		detail.setCredit(getLoan().getAmount());
		detail.setBalancingAccount(rBankAccount);
		accountEntryDetailBean.insert(detail);
		// Segundo Apunte
		detail = new AccountEntryDetail();
		detail.setAccount(rBankAccount);
		detail.setAccountEntry(entry);
		detail.setConcept(getLoan().getDescription());
		detail.setDebit(getLoan().getAmount() - getLoan().getExpenses());
		detail.setBalancingAccount(loanAccount);
		accountEntryDetailBean.insert(detail);
		// Tercer Apunte
		detail = new AccountEntryDetail();
		detail.setAccount(AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_FINAN_EXPENSES_ACC));
		detail.setAccountEntry(entry);
		detail.setConcept(getLoan().getDescription());
		detail.setDebit(getLoan().getExpenses());
		detail.setBalancingAccount(loanAccount);
		accountEntryDetailBean.insert(detail);
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

	
}
