package com.code.aon.ui.accounting.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountBudget;
import com.code.aon.accounting.AccountBudgetDetail;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountSummary;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.event.AccountSummaryManager;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.controller.BalanceSheetController.BalanceItem;
import com.code.aon.ui.util.AonUtil;

public class AccountChangeController {

	private Account initAccount;
	private Account balancingAccount;
	private Account finalAccount;
	private Date fromDate;
	private Date toDate;
	private Period period;
	private SecurityLevel securityLevel;
	private List<ITransferObject> accountDetailList;
	private List<ITransferObject> accountSummaryList;
	private static final Logger LOGGER = Logger.getLogger(AccountEntryDetail.class.getName());
	private Integer count;
	
	public void onReset(ActionEvent e){
		setInitAccount(null);
		setBalancingAccount(null);
		setFinalAccount(null);
		setFromDate(null);
		setToDate(null);
		setPeriod(null);
		setSecurityLevel(null);
		setCount(0);
	}
	

	public void onChangeAccounts(ActionEvent e) throws ManagerBeanException {
		if(!finalAccount.isEntryEnabled()){
			String msg ="La Cuenta Destino no permite apuntes";
				LOGGER.log(Level.SEVERE, msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
		}
		
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
                count=0;
				IManagerBean bean;
				bean = BeanManager.getManagerBean(AccountEntryDetail.class);

				String accountInit = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID);
				String accountBalancing = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT_ID);
				String date = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE);
				String accperiod = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD);
				String security = bean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_SECURITY_LEVEL);

				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountInit, initAccount.getId());
				criteria.addEqualExpression(accperiod, period.getId());

				if (fromDate != null) {
					criteria.addGreaterThanOrEqualExpression(date, fromDate);
				}
				if (fromDate != null) {
					criteria.addLessThanOrEqualExpression(date, toDate);
				}
				if (securityLevel != null) {
					criteria.addEqualExpression(security, securityLevel);
				}
				if (balancingAccount != null) {
					criteria.addEqualExpression(accountBalancing,
							balancingAccount.getId());
				}

				setAccountDetailList(bean.getList(criteria));

				for (ITransferObject to : accountDetailList) {
					AccountEntryDetail acc = (AccountEntryDetail) to;
					acc.setAccount(finalAccount);
					bean.update(acc);
					count++;
				}

				Criteria criteria2 = new Criteria();
				criteria2.addEqualExpression(accountBalancing, initAccount
						.getId());
				criteria2.addEqualExpression(accperiod, period.getId());
				if (fromDate != null) {
					criteria2.addGreaterThanOrEqualExpression(date, fromDate);
				}
				if (fromDate != null) {
					criteria2.addLessThanOrEqualExpression(date, toDate);
				}
				if (securityLevel != null) {
					criteria2.addEqualExpression(security, securityLevel);
				}

				setAccountDetailList(bean.getList(criteria2));

				for (ITransferObject to : accountDetailList) {
					AccountEntryDetail acc = (AccountEntryDetail) to;
					acc.setBalancingAccount(finalAccount);
					bean.update(acc);
					count++;
				}
				
				IManagerBean summmaryBean;
				summmaryBean = BeanManager.getManagerBean(AccountSummary.class);
				Criteria criteria3 = new Criteria();
				criteria3.addEqualExpression(accountBalancing, initAccount
						.getId());
				criteria3.addEqualExpression(accperiod, period.getId());
				if (fromDate != null) {
					criteria3.addGreaterThanOrEqualExpression(date, fromDate);
				}
				if (fromDate != null) {
					criteria3.addLessThanOrEqualExpression(date, toDate);
				}
				if (securityLevel != null) {
					criteria3.addEqualExpression(security, securityLevel);
				}
				setAccountSummaryList(summmaryBean.getList(criteria2));

				for (ITransferObject to : accountSummaryList) {
					AccountSummary acc = (AccountSummary) to;
					if(acc.getCredit()==0 && acc.getDebit()==0){
						summmaryBean.remove(acc);					
					}					
				}
				
				AonUtil.addInfoMessage("Se han cambiado "+count+" apuntes");
				
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception ex) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.log(Level.SEVERE, msg, ex);
				}
				String msg = "Error on aon-account:  " + ex.getMessage();
				LOGGER.log(Level.SEVERE, msg, ex);
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

	public List<ITransferObject> getAccountDetailList() {
		return accountDetailList;
	}

	public void setAccountDetailList(List<ITransferObject> accountDetailList) {
		this.accountDetailList = accountDetailList;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Account getInitAccount() {
		return initAccount;
	}

	public void setInitAccount(Account initAccount) {
		this.initAccount = initAccount;
	}

	public Account getBalancingAccount() {
		return balancingAccount;
	}

	public void setBalancingAccount(Account balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

	public Account getFinalAccount() {
		return finalAccount;
	}

	public void setFinalAccount(Account finalAccount) {
		this.finalAccount = finalAccount;
	}

	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public List<ITransferObject> getAccountSummaryList() {
		return accountSummaryList;
	}



	public void setAccountSummaryList(List<ITransferObject> accountSummaryList) {
		this.accountSummaryList = accountSummaryList;
	}

	

}