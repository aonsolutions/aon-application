package com.code.aon.ui.product.event;

import java.util.Date;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class CatalogueSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (getStartDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CATALOGUE_START_DATE), getStartDateFrom());
		}
		if (getStartDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.CATALOGUE_START_DATE), getStartDateTo());
		}
		if (getEndDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.CATALOGUE_END_DATE), getEndDateFrom());
		}
		if (getEndDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.CATALOGUE_END_DATE), getEndDateTo());
		}
	}	
}