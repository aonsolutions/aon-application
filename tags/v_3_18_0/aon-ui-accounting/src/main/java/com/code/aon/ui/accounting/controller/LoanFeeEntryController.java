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
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.LoanFeeEntryHeader;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class LoanFeeEntryController implements ISpecialAccountEntry{

	private static final Logger LOGGER = Logger.getLogger(LoanFeeEntryController.class.getName()); 
	
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	private static final String STATEMENT_CONTROLLER_NAME = "statement";
	
	private boolean isNew;
	
	private AccountEntry accountEntry;
	
	private LoanFeeEntryHeader header;

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

	public LoanFeeEntryHeader getHeader() {
		return header;
	}

	public void setHeader(LoanFeeEntryHeader header) {
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
	
	private LoanFeeEntryHeader initializeHeader() throws ManagerBeanException {
		LoanFeeEntryHeader header = new LoanFeeEntryHeader();
		header.setFeePeriod(AccountingPeriodUtil.getDefaultPeriod());
		header.setFeeDate(new Date());
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
				if(!this.isNew){
					deleteAccountEntryDetails(getAccountEntry());
					entry = this.getAccountEntry();
				}
				entry.setEntryDate(getHeader().getFeeDate());
				entry.setAccountPeriod(getHeader().getFeePeriod().getId());
				entry.setType(AccountEntryType.LOAN_FEE);
				entry.setSecurityLevel(getHeader().getLoan().getSecurityLevel());
				if (this.isNew){
					entry = (AccountEntry)entryBean.insert(entry);
				} else{
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
		Account rBankAccount = AccountUtil.obtainRBankAccount(getHeader().getLoan().getRegistryBank());
		Account loanAccount = obtainLoanAccount(getHeader().getLoan());
		detail.setAccount(rBankAccount);
		detail.setAccountEntry(entry);
		detail.setConcept(getHeader().getDescription());
		detail.setCredit(getHeader().getAmortization() + getHeader().getInterest());
		detail.setBalancingAccount(loanAccount);
		accountEntryDetailBean.insert(detail);
		// Segundo Apunte
		detail = new AccountEntryDetail();
		detail.setAccount(loanAccount);
		detail.setAccountEntry(entry);
		detail.setConcept(getHeader().getDescription());
		detail.setDebit(getHeader().getAmortization());
		detail.setBalancingAccount(rBankAccount);
		accountEntryDetailBean.insert(detail);
		// Tercer Apunte
		detail = new AccountEntryDetail();
		detail.setAccount(AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT));
		detail.setAccountEntry(entry);
		detail.setConcept(getHeader().getDescription());
		detail.setDebit(getHeader().getInterest());
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
	
	/* NO se llama a AccountUtil porque este aquí no se genera si no existe */	
	@SuppressWarnings("unchecked")
	private Account obtainLoanAccount(Loan loan) throws ManagerBeanException {
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			LoanAccount loanAccount = (LoanAccount)iter.next();
			return loanAccount.getAccount();
		}
		return null;
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
		LoanFeeEntryHeader header = new LoanFeeEntryHeader();
		Loan loan = obtainLoan(entry);
		AccountEntryDetail accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.LONG_TERM_LOAN_ACCOUNT_PREFIX + "*");
		if (accountEntryDetail == null) {
			accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.SHORT_TERM_LOAN_ACCOUNT_PREFIX + "*");	
		}
		header.setAmortization(accountEntryDetail.getDebit());
		header.setDescription(accountEntryDetail.getConcept());
		header.setFeePeriod(new Period());
		header.getFeePeriod().setId(entry.getAccountPeriod());
		header.setFeeDate(entry.getEntryDate());
		header.setLoan(loan);
		accountEntryDetail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT).getId() + "*");
		header.setInterest(accountEntryDetail.getDebit());
		setHeader(header);
	}
	
	@SuppressWarnings("unchecked")
	private Loan obtainLoan(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.LONG_TERM_LOAN_ACCOUNT_PREFIX + "*");
		if (detail == null) {
			detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, AccountConstants.SHORT_TERM_LOAN_ACCOUNT_PREFIX + "*");	
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
		return "account_loan_fee_entry";
	}
	
	public Account getRelatedAccount() throws ManagerBeanException {
		return obtainLoanAccount( getHeader().getLoan() );	
	}
	
	public double getOutstandingBalance() {
		try {
			SummaryProvider sp = new SummaryProvider();
			SummaryProviderParameters params = new SummaryProviderParameters();
			params.setAccountExpression( getRelatedAccount().getId());
			params.setAccountLevel(5);
			params.setBudgeted(false);
			Period period = AccountingPeriodUtil.getPeriod( getHeader().getFeeDate() );
			params.setPeriod(period);
			params.setFromDate(period.getInitiationDate());
			params.setToDate(period.getDeadline());
			params.setSecurityLevel(getHeader().getLoan().getSecurityLevel());
			SummaryCollection sc = sp.getSummaryCollection(params);
			return sc.getCreditBalance();
		} catch (ManagerBeanException e) {
			LOGGER.warning("No se puede obtener el saldo pendiente: " + e.getMessage());
		}
		return 0;
	}

	public void onAccountStatement(ActionEvent event) {
		try {
			Account account = getRelatedAccount();
			StatementController c = (StatementController) AonUtil.getRegisteredBean(STATEMENT_CONTROLLER_NAME);
			c.onReset(event);
			SummaryProviderParameters spp = new SummaryProviderParameters();
			spp.setAccountExpression(account.getId());
			
			Period period = AccountingPeriodUtil.getPeriod( getHeader().getFeeDate() );
			spp.setPeriod(period);
			spp.setFromDate(period.getInitiationDate());
			spp.setToDate(period.getDeadline());
			spp.setSecurityLevel(getHeader().getLoan().getSecurityLevel());
			c.setParams(spp);
			c.setBackAction("account_loan_fee_entry");
			
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IAccountAlias.ACCOUNT_ID);
			criteria.addExpression(alias, account.getId() + "*");
			alias = c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
			criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));
			
			c.onSearch(event);
			if (c.getModel().getRowCount() > 0) {
				c.getModel().setRowIndex(0);
				c.onSelect(event);
			} else {
				String msg = "No existen cuentas contables para la cuenta.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}			

		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ExpressionException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
}