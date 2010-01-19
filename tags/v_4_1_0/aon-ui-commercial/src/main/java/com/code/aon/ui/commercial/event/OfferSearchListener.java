package com.code.aon.ui.commercial.event;

import java.util.Date;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class OfferSearchListener extends ControllerSearchListener {

	private Date issueDateFrom;
	
	private Date issueDateTo;

	private Target target;

	public Date getIssueDateFrom() {
		return issueDateFrom;
	}

	public void setIssueDateFrom(Date issueDateFrom) {
		this.issueDateFrom = issueDateFrom;
	}

	public Date getIssueDateTo() {
		return issueDateTo;
	}

	public void setIssueDateTo(Date issueDateTo) {
		this.issueDateTo = issueDateTo;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setIssueDateFrom(null);
		setIssueDateTo(null);
		setTarget(new Target());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getIssueDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_ISSUE_DATE), getIssueDateFrom());
		}
		if (getIssueDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_ISSUE_DATE), getIssueDateTo());
		}
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_TARGET_ID), getTarget().getId());			
		}
	}	
}