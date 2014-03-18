package com.code.aon.accounting.util;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountingUtil implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public static Account obtainDefaultAccount(AppParam param) throws ManagerBeanException {
		Integer id = AppParamUtil.getValueAsInteger(param);
		if ( id != null ) {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			return (Account) accountBean.get(id);
		} else {
			String value = AppParamUtil.getValue(param);
			if (! StringUtils.isEmpty(value) ) {
				throw new ManagerBeanException("No existe la cuenta contable número: " + value);
			}	
		}
		return null;
	}
	
	public Account obtainCashAccount() throws ManagerBeanException {
		return obtainDefaultAccount(AppParam.ACC_DEFAULT_CASH_ACC);
	}

	public Date getFirstPeriodInitialDate() throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		String alias = periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE);
		criteria.addOrder(alias);
		Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return ((Period) iter.next()).getInitiationDate();
		}
		return null;
	}

	public Date getLastPeriodDeadline() throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		String alias = periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE);
		criteria.addOrder(alias,false);
		Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return ((Period) iter.next()).getDeadline();
		}
		return null;
	}

	public Period getPeriod(Date date) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		criteria.addGreaterThanOrEqualExpression(periodBean
				.getFieldName(IEntityAlias.PERIOD_DEADLINE), date);
		criteria.addLessThanOrEqualExpression(periodBean
				.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE), date);
		Iterator<ITransferObject> iter = periodBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			return (Period) iter.next();
		}
		return null;
	}

	public Period getPreviousPeriod(Period period) throws ManagerBeanException {
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		String alias = periodBean.getFieldName(IEntityAlias.PERIOD_INITIATION_DATE);
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
			int year = initiation.get(Calendar.YEAR);
			if (year < 2000 && year > 2100) {
				throw new ManagerBeanException("No se puede crear el elercicio contable. Revise el año de la fecha.");
			}
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			period = new Period();
			period.setName(Integer.toString(year));
			period.setInitiationDate(initiation.getTime());
			period.setDeadline(deadline.getTime());
			period.setStatus(AccountPeriodStatus.ACTIVE);
			period =(Period) periodBean.insert(period);
		}
		return period;
	}

	public boolean existsEntry(Period period, AccountEntryType accountEntryType, SecurityLevel securityLevel, Integer accountEntryId) throws ManagerBeanException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(entryBean
				.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD_ID), period.getId());
		criteria.addEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_TYPE),
				accountEntryType);
		if (accountEntryId != null) {
			Expression exp = ExpressionUtilities.getNotEqualExpression(entryBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID),accountEntryId);
			criteria.addExpression(exp);
		}
		if (securityLevel != null) {
			criteria.addEqualExpression(entryBean
					.getFieldName(IEntityAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), securityLevel);
		}
		List<ITransferObject> list = entryBean.getList(criteria);
		return (list.size() > 0);
	}
	
	public boolean existsEntry(Period period, AccountEntryType accountEntryType, SecurityLevel securityLevel) throws ManagerBeanException {
		return existsEntry(period, accountEntryType, securityLevel,null);
	}
	public boolean existsOpeningEntry(Period period, SecurityLevel securityLevel) throws ManagerBeanException {
		return existsEntry(period, AccountEntryType.OPENING, securityLevel,null);
	}
	public boolean existsOperatingEntry(Period period, SecurityLevel securityLevel) throws ManagerBeanException {
		return existsEntry(period, AccountEntryType.OPERATING, securityLevel,null);
	}
	public boolean existsClosingEntry(Period period, SecurityLevel securityLevel) throws ManagerBeanException {
		return existsEntry(period, AccountEntryType.CLOSING, securityLevel,null);
	}

	
	// TODO ¿?
	public AccountEntryDetail getEntryDetailFromAccountPattern(AccountEntry entry, String accountPattern) throws ManagerBeanException{
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
			criteria.addExpression(accountEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_CODE), accountPattern);
			Iterator<?> iter = accountEntryDetailBean.getList(criteria).iterator();
			return iter.hasNext()?(AccountEntryDetail)iter.next():null;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}
}
