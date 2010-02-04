package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class OfferSearchListener extends ControllerSearchListener {

	private Target target;

	private OfferStatus[] offerStatuses;

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public OfferStatus[] getOfferStatuses() {
		return offerStatuses;
	}

	public void setOfferStatuses(OfferStatus[] offerStatuses) {
		this.offerStatuses = offerStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setTarget(new Target());
		OfferStatus[] defaultOfferStatus = {OfferStatus.PENDING};
		setOfferStatuses(defaultOfferStatus);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_TARGET_ID), getTarget().getId());			
		}
		if (!ArrayUtils.isEmpty(getOfferStatuses())) {
			String status = getController().resolveAlias(ICommercialAlias.OFFER_STATUS);
			addEnumToCriteria(criteria, status, getOfferStatuses());
		}
	}	

}