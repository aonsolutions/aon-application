package com.code.aon.ui.accounting.controller.entry;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.LoanFeeEntry;
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
import com.code.aon.common.util.CommonUtil;
import com.code.aon.entity.IEntityAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.report.StatementController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class LoanFeeEntryController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoanFeeEntryController.class.getName()); 
	
	private LoanFeeEntry entry;
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

	public LoanFeeEntry getEntry() {
		return entry;
	}

	public void setEntry(LoanFeeEntry entry) {
		this.entry = entry;
	}

	public void onReset(ActionEvent event){
		try {
			setEntry(new LoanFeeEntry());
			getEntry().setFeePeriod(AccountingPeriodUtil.getDefaultPeriod());
			getEntry().setFeeDate(new Date());
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
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
				entry.setEntryDate(getEntry().getFeeDate());
				entry.setAccountPeriod(getEntry().getFeePeriod().getId());
				entry.setType(AccountEntryType.LOAN_FEE);
				entry.setSecurityLevel(getEntry().getLoan().getSecurityLevel());
				entry = (AccountEntry)entryBean.insert(entry);
				insertEntryDetails(entry);
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
	
	private void insertEntryDetails(AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		// Primer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		Account rBankAccount = getAccountBridgeUtil().obtainRBankAccount(getEntry().getLoan().getRegistryBank());
		Account loanAccount = obtainLoanAccount(getEntry().getLoan());
		detail.setAccount(rBankAccount);
		detail.setAccountEntry(entry);
		detail.setConcept(getEntry().getDescription());
		detail.setCredit(getEntry().getFee());
		detail.setBalancingAccount(loanAccount);
		accountEntryDetailBean.insert(detail);
		// Segundo Apunte
		detail = new AccountEntryDetail();
		detail.setAccount(loanAccount);
		detail.setAccountEntry(entry);
		detail.setConcept(getEntry().getDescription());
		detail.setDebit(getEntry().getAmortization());
		detail.setBalancingAccount(rBankAccount);
		accountEntryDetailBean.insert(detail);
		// Tercer Apunte
		if (CommonUtil.round(getEntry().getInterest()) != 0.0) {
			detail = new AccountEntryDetail();
			detail.setAccount(getEntry().getInterestAccount());
			detail.setAccountEntry(entry);
			detail.setConcept(getEntry().getDescription());
			detail.setDebit(getEntry().getInterest());
			detail.setBalancingAccount(loanAccount);
			accountEntryDetailBean.insert(detail);
		}
		// Cuarto Apunte
		if (CommonUtil.round(getEntry().getExpenses()) != 0.0) {
			detail = new AccountEntryDetail();
			detail.setAccount(getEntry().getExpensesAccount());
			detail.setAccountEntry(entry);
			detail.setConcept(getEntry().getDescription());
			detail.setDebit(getEntry().getExpenses());
			detail.setBalancingAccount(loanAccount);
			accountEntryDetailBean.insert(detail);
		}
	}
	
	/* NO se llama a AccountUtil porque este aquí no se genera si no existe */	
	private Account obtainLoanAccount(Loan loan) throws ManagerBeanException {
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
		Iterator<?> iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			LoanAccount loanAccount = (LoanAccount)iter.next();
			return loanAccount.getAccount();
		}
		return null;
	}
	
	private void loadAccountEntryController(AccountEntry entry) throws ManagerBeanException {
		AccountEntryController entryController = (AccountEntryController)FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
		entryController.setCriteria(criteria);
		entryController.onSearch(null);
		entryController.getModel().setRowIndex(0);
		entryController.onSelect(null);
	}

	
	public Account getRelatedAccount() throws ManagerBeanException {
		return obtainLoanAccount( getEntry().getLoan() );	
	}
	
	public Double getOutstandingBalance() {
		try {
			SummaryProvider sp = new SummaryProvider();
			SummaryProviderParameters params = new SummaryProviderParameters();
			if (getRelatedAccount() != null) {
				params.setAccountExpression( getRelatedAccount().getId());
				params.setAccountLevel(5);
				params.setFromDate(getEntry().getLoan().getLoanDate());
				params.setSecurityLevel(getEntry().getLoan().getSecurityLevel());
				SummaryCollection sc = sp.getSummaryCollection(params);
				double p = CommonUtil.round(sc.getCreditBalance())==0.0?
						CommonUtil.round(sc.getUnpaidBalance()*-1):sc.getCreditBalance(); 
				return p;
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("No se puede obtener el saldo pendiente: " + e.getMessage());
		}
		return null;
	}

	public void onAccountStatement(ActionEvent event) {
		try {
			Account account = getRelatedAccount();
			StatementController c = (StatementController) AonUtil.getRegisteredBean(IAccountingConstants.STATEMENT_CONTROLLER_NAME);
			c.onReset(event);
			SummaryProviderParameters spp = new SummaryProviderParameters();
			spp.setAccountExpression(account.getId());
			
			Period period = getAccountingUtil().getPeriod( getEntry().getFeeDate() );
			spp.setPeriod(period);
			spp.setFromDate(period.getInitiationDate());
			spp.setToDate(period.getDeadline());
			spp.setSecurityLevel(getEntry().getLoan().getSecurityLevel());
			c.setParams(spp);
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IEntityAlias.ACCOUNT_ID);
			criteria.addExpression(alias, account.getId() + IAccountingConstants.ASTERISK);
			alias = c.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED);
			criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));
			c.onSearch(event);
			if (c.getModel().getRowCount() > 0) {
				c.getModel().setRowIndex(0);
				c.onSelect(event);
				c.setBackAction(IAccountingConstants.ACCOUNT_LOAN_FEE_ENTRY_NAVKEY);
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