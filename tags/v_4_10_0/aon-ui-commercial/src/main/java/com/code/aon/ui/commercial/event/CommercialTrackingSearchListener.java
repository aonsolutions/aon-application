package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class CommercialTrackingSearchListener extends ControllerSearchListener {

	private Seller seller;
	
	private Target target;
		
	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setSeller( new Seller() );
		setTarget( new Target() );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getSeller() != null) && (getSeller().getId() != null) ) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_SELLER_ID);
			criteria.addEqualExpression(alias, getSeller().getId());			
		}
		if ( (getTarget() != null) && (getTarget().getId() != null) ) {
			String alias = getFieldName(ICommercialAlias.COMMERCIAL_TRACKING_TARGET_ID);
			criteria.addEqualExpression(alias, getTarget().getId());			
		}
	}
	
}