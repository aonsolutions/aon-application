package com.code.aon.accounting.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountSummary;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionException;

public class AccountingUtil {

	public Account obtainDefaultAccount(String defaultAccountName) throws ManagerBeanException {
		IManagerBean accAppParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accAppParamBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), defaultAccountName);
		Iterator<ITransferObject> iter = accAppParamBean.getList(criteria).iterator();
		if(iter.hasNext()){
			ApplicationParameter param = (ApplicationParameter)iter.next();
			IManagerBean accountBean  = BeanManager.getManagerBean(Account.class);
			return (Account) accountBean.get(param.getValue());
		}
		return null;
	}

	public Account obtainCashAccount() throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), obtainDefaultAccount(DefaultAccounts.CASH_ACCOUNT).getId());
		List<ITransferObject> list = accountBean.getList(criteria);
		if(list.size() > 0){
			return (Account)list.iterator().next();
		}
		return null;
	}

	public Period getPeriod(Date date) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(periodBean
				.getFieldName(IAccountingAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean
				.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE), date);
		Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (Period) iter.next();
		}
		return null;
	}

	public Period getPreviousPeriod(Period period) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		String alias = periodBean.getFieldName(IAccountingAlias.PERIOD_INITIATION_DATE);
		criteria.addLessThanExpression(alias, period.getInitiationDate());
		criteria.addOrder(alias,false);
		Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (Period) iter.next();
		}
		return null;
	}

	public Period obtainPeriod(Date date) throws ManagerBeanException {
		Period period = getPeriod(date);
		if (period == null) {
			Calendar initiation = new GregorianCalendar();
			initiation.setTime(date);
			initiation.set(Calendar.DAY_OF_MONTH, 1);
			initiation.set(Calendar.MONTH, 0);
			Calendar deadline = new GregorianCalendar();
			deadline.setTime(date);
			deadline.set(Calendar.DAY_OF_MONTH, 31);
			deadline.set(Calendar.MONTH, 11);

			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			period = new Period();
			period.setId(Integer.toString(initiation.get(Calendar.YEAR)));
			period.setInitiationDate(initiation.getTime());
			period.setDeadline(deadline.getTime());
			period =(Period) periodBean.insert(period);
		}
		return period;
	}

	public Balance getOpeningEntryBalance(Date date, String accountId) throws ManagerBeanException {
		return getAccountEntryBalance(date, accountId, AccountEntryType.OPENING);
	}

	public Balance getClosingEntryBalance(Date date, String accountId) throws ManagerBeanException {
		return getAccountEntryBalance(date, accountId, AccountEntryType.CLOSING);
	}

	public Balance getAccountEntryBalance(Date date, String accountId, AccountEntryType type)
			throws ManagerBeanException {
		if (date == null) {
			throw new IllegalArgumentException("date param must not be null.");
		}
		if (accountId == null) {
			throw new IllegalArgumentException("accountId param must not be null.");
		}
		if (type == null) {
			throw new IllegalArgumentException("type param must not be null.");
		}
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria c = new Criteria();
		String alias = entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ENTRY_DATE);
		c.addLessThanOrEqualExpression(alias, date);
		c.addOrder(alias, false);
		c.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE), type);
		List<ITransferObject> list = entryBean.getList(c);
		if (list.size() == 0) {
			return null;
		}
		IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		double debit = 0;
		double credit = 0;
		AccountEntry entry = (AccountEntry) list.get(0);
		Integer id = entry.getId();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryDetailBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
		criteria.addEqualExpression(entryDetailBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), accountId);
		List<ITransferObject> details = entryDetailBean.getList(criteria);
		if (details.size() > 0) {
			for (ITransferObject to : details) {
				AccountEntryDetail detail = (AccountEntryDetail) to;
				debit = CommonUtil.round(debit + detail.getDebit());
				credit = CommonUtil.round(credit + detail.getCredit());
			}
		}
		Balance balance = null;
		if (debit != 0 || credit != 0) {
			balance = new Balance();
			balance.setAccountEntry(entry.getId());
			balance.setFromDate(entry.getEntryDate());
			balance.setDebit(debit);
			balance.setCredit(credit);
			balance.setUnpaidBalance(debit);
			balance.setCreditBalance(credit);
		}
		return balance;
	}

	@SuppressWarnings("unchecked")
	public Balance getPeriodBalance(Date fromDate, Date toDate, String accountId,
			boolean excludeOpeningEntry, boolean excludeClosingEntry) throws ManagerBeanException {
		IManagerBean sumBean = BeanManager.getManagerBean(AccountSummary.class);
		String dateAlias = sumBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_ENTRY_DATE);
		String accountAlias = sumBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_ACCOUNT_ID);
		String debitAlias = sumBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_DEBIT);
		String creditAlias = sumBean.getFieldName(IAccountingAlias.ACCOUNT_SUMMARY_CREDIT);
		ProjectionList pl = new ProjectionList();
		pl.add(Projection.sum(debitAlias));
		pl.add(Projection.sum(creditAlias));
		Criteria c = new Criteria();
		c.addEqualExpression(accountAlias, accountId);
		if (fromDate != null) {
			c.addGreaterThanOrEqualExpression(dateAlias, fromDate);
		}
		if (toDate != null) {
			c.addLessThanOrEqualExpression(dateAlias, toDate);
		}
		List list = sumBean.getList(pl, c);
		if (list.size() == 0) {
			return null;
		}
		Object[] sums = (Object[]) list.get(0);
		Double debit = sums[0] != null ? (Double) sums[0] : new Double(0);
		Double credit = sums[1] != null ? (Double) sums[1] : new Double(0);
		Balance balance = new Balance();
		balance.setFromDate(fromDate);
		balance.setToDate(toDate);
		balance.setDebit(debit);
		balance.setCredit(credit);
		if (excludeOpeningEntry) {
			substractAmounts(balance, fromDate, accountId, AccountEntryType.OPENING);
		}
		if (excludeClosingEntry) {
			substractAmounts(balance, fromDate, accountId, AccountEntryType.CLOSING);
		}
		double bal = CommonUtil.round(debit - credit);
		if (bal > 0) {
			balance.setUnpaidBalance(bal);
		} else {
			balance.setCreditBalance(CommonUtil.round(bal * (-1)));
		}
		return balance;
	}

	private void substractAmounts(Balance balance, Date fromDate, String accountId,
			AccountEntryType type) throws ManagerBeanException {
		Balance openingBalance = getAccountEntryBalance(fromDate, accountId, type);
		if (openingBalance != null) {
			boolean inRange = false;
			if (fromDate != null) {
				Date openingEntryDate = openingBalance.getFromDate();
				inRange = (fromDate.compareTo(openingEntryDate) > 0);
			}
			if (!inRange) {
				balance.setDebit(CommonUtil.round(balance.getDebit() - openingBalance.getDebit()));
				balance.setCredit(CommonUtil.round(balance.getCredit() - openingBalance.getCredit()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public AccountEntryDetail getEntryDetailFromAccountPattern(AccountEntry entry, String accountPattern) throws ManagerBeanException{
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
			criteria.addExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), accountPattern);
			Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
			return iter.hasNext()?(AccountEntryDetail)iter.next():null;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}
	
	public boolean existsEntry(Period period, AccountEntryType accountEntryType,
			SecurityLevel securityLevel) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), period.getId());
		criteria.addEqualExpression(entryBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_TYPE),
				accountEntryType);
		criteria.addEqualExpression(entryBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel);
		List<ITransferObject> list = entryBean.getList(criteria);
		return (list.size() > 0);
	}
}
