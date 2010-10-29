package com.code.aon.ui.finance.event;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class BankStatementSearchListener extends ControllerSearchListener {

	private Date fromDate;
	private Date toDate;
	private String description;
	private String amount;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setFromDate(null);
		setToDate(null);
		setDescription(null);
		setAmount(null);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_OPERATION_DATE), getFromDate());			
		}
		if (getToDate() != null) {
			criteria.addLessThanOrEqualExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_OPERATION_DATE), getToDate());			
		}
		if (StringUtils.isNotEmpty(getDescription())) {
			criteria.addExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_DESCRIPTION), getDescription());			
		}
		if (StringUtils.isNotEmpty(getAmount())) {
			criteria.addExpression(getFieldName(IFinanceAlias.BANK_STATEMENT_AMOUNT), getAmount());			
		}
	}

}