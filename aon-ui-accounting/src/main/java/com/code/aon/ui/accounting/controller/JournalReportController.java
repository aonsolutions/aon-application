package com.code.aon.ui.accounting.controller;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class JournalReportController extends BasicController {

	private Period period;
	private Date fromDate;
	private Date toDate;
	private Date date;

	private SecurityLevel securityLevel;

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public void onReset(ActionEvent event) {
		initialize();
		super.onReset(event);
	}

	private void initialize() {
		setPeriod(null);
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
		setSecurityLevel(null);
	}

	public void onEditSearch(ActionEvent event) {
		initialize();
		super.onEditSearch(event);
	}

	public void onSearch(ActionEvent event) {
		try {
			Criteria criteria = getCriteria();
			if (period != null) {
				criteria.addEqualExpression(getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ACCOUNT_PERIOD), period.getId());
			}
			if (getFromDate() != null) {
				criteria.addGreaterThanOrEqualExpression(getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), getFromDate());
			}
			if (getToDate() != null) {
				criteria.addLessThanOrEqualExpression(getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ENTRY_DATE), getToDate());
			}
			if (getSecurityLevel() != null) {
				//TODO Alias para el securityLevel
				criteria.addEqualExpression("AccountEntryDetail.accountEntry.securityLevel", getSecurityLevel());
			}
			getCriteria().addOrder(getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID));
			super.onSearch(event);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}
}
