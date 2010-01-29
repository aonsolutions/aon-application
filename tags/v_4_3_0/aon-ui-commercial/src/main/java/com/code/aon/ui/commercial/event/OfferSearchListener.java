package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class OfferSearchListener extends ControllerSearchListener {

	private Target target;

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setTarget(new Target());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_TARGET_ID), getTarget().getId());			
		}
	}	
}