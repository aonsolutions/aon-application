package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class LoanEntryController implements ISpecialAccountEntry {

	private static final Logger LOGGER = Logger.getLogger(LoanEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private Loan loan;

	private AccountingUtil accountingUtil;

	public AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
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

	public Loan getLoan() {
		return loan;
	}

	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	
	public void onReset(ActionEvent event){
		reset();
	}
	
	private void reset(){
		this.isNew = true;
		this.loan = initializeLoan();
	}
	
	private Loan initializeLoan() {
		Loan loan = new Loan();
		loan.setLoanDate(new Date());
		return loan;
	}
	
	public void accept(ActionEvent event) {

		try {
			Integer.parseInt( loan.getTerm() );
		} catch (NumberFormatException e) {
			String msg = "El plazo de la operación no es un valor numérico válido";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException( msg );
		}
		
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
				AccountingPeriodUtil.validateAccountPeriod(getLoan().getLoanDate());
				AccountEntry entry = new AccountEntry();
				entry.setEntryDate(getLoan().getLoanDate());
				entry.setAccountPeriod(AccountUtil.obtainPeriod(getLoan().getLoanDate()).getId());
				entry.setJournal(null);
				entry.setType(AccountEntryType.LOAN);
				entry.setSecurityLevel(getLoan().getSecurityLevel());
				IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
				IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
				if(this.isNew){
					loanBean.insert(getLoan());
					entry = (AccountEntry)entryBean.insert(entry);
				} else {
					deleteAccountEntryDetails(getAccountEntry());
					loan = (Loan) HibernateUtil.getSession(sessionName).merge(loan);
					loan = (Loan) loanBean.update(loan);
					
					// La cuenta contable puede cambiar en función del plazo del préstamo.
					// Nos aseguramos de que el enlace entre cuenta y prestamo sea correcto.
					deleteLoanAccount(getLoan());
					
					entry = this.getAccountEntry();
					entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
					entry = (AccountEntry)entryBean.update(entry);
					
				}
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
					deleteLoanAccount(getLoan());
					deleteLoan(getLoan());
				} catch (ManagerBeanException e) {
					throw new ManagerBeanException("Error Removing Loan", e);
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
		// Primer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		Account loanAccount = AccountUtil.obtainLoanAccount(getLoan());
		Account rBankAccount = AccountUtil.obtainRBankAccount(getLoan().getRegistryBank());
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
		detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.FINANCIAL_EXPENSES_ACCOUNT));;
		detail.setAccountEntry(entry);
		detail.setConcept(getLoan().getDescription());
		detail.setDebit(getLoan().getExpenses());
		detail.setBalancingAccount(loanAccount);
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
	private void deleteLoanAccount(Loan loan) throws ManagerBeanException {
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			LoanAccount loanAccount  = (LoanAccount)iter.next();
			loanAccountBean.remove(loanAccount);
		}
	}

	private void deleteLoan(Loan loan) throws ManagerBeanException {
		IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
		loanBean.remove(loan);
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
		Loan loan = obtainLoan(entry);
		setLoan(loan);
	}

	@SuppressWarnings("unchecked")
	private Loan obtainLoan(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.SHORT_TERM_LOAN_ACCOUNT_PREFIX + "*");
		if (detail == null) {
			detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.LONG_TERM_LOAN_ACCOUNT_PREFIX + "*");	
		}
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_ACCOUNT_ID), detail.getAccount().getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LoanAccount)iter.next()).getLoan();
		}
		return null;
	}

	@Override
	public String getNavigationKey() {
		return "account_loan_entry";
	}
	
	public String getPeriodMessage() {
		try {
			return AccountingPeriodUtil.getValidAccountPeriod(getLoan().getLoanDate());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return " - ";
		}
	}
}
