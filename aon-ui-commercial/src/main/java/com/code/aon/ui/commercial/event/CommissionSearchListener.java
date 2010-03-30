package com.code.aon.ui.commercial.event;

import java.util.Date;

import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class CommissionSearchListener extends ControllerSearchListener {

	private Date startDateFrom;
	
	private Date startDateTo;
	
	private Date endDateFrom;
	
	private Date endDateTo;
	
	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setStartDateFrom(null);
		setStartDateTo(null);
		setEndDateFrom(null);
		setEndDateTo(null);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getStartDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getController().getFieldName(ICommercialAlias.COMMISSION_START_DATE), getStartDateFrom());
		}
		if (getStartDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getController().getFieldName(ICommercialAlias.COMMISSION_START_DATE), getStartDateTo());
		}
		if (getEndDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getController().getFieldName(ICommercialAlias.COMMISSION_END_DATE), getEndDateFrom());
		}
		if (getEndDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getController().getFieldName(ICommercialAlias.COMMISSION_END_DATE), getEndDateTo());
		}
	}	
}