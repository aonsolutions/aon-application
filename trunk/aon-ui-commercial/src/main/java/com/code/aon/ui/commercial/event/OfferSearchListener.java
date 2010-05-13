package com.code.aon.ui.commercial.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.seller.Seller;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class OfferSearchListener extends ControllerSearchListener {

	private Target target;

	private OfferType offerType;

	private Target thirdParty;

	private Seller seller;
	
	private OfferStatus[] offerStatuses;

	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public OfferType getOfferType() {
		return offerType;
	}

	public void setOfferType(OfferType offerType) {
		this.offerType = offerType;
	}

	public Target getThirdParty() {
		return thirdParty;
	}

	public void setThirdParty(Target thirdParty) {
		this.thirdParty = thirdParty;
	}

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public OfferStatus[] getOfferStatuses() {
		return offerStatuses;
	}

	public void setOfferStatuses(OfferStatus[] offerStatuses) {
		this.offerStatuses = offerStatuses;
	}

	public boolean isThirdPartyType() {
		return OfferType.THIRD_PARTY == offerType;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setTarget(new Target());
		setSeller(new Seller());
		setOfferType(null);
		setThirdParty(new Target());
		OfferStatus[] defaultOfferStatus = {OfferStatus.PENDING};
		setOfferStatuses(defaultOfferStatus);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getTarget() != null && getTarget().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_TARGET_ID), getTarget().getId());			
		}
		if (getSeller() != null && getSeller().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_SELLER_ID), getSeller().getId());			
		}
		if (getOfferType() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_TYPE), getOfferType());			
		}
		if (getThirdParty() != null && getThirdParty().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ICommercialAlias.OFFER_THIRD_PARTY_ID), getThirdParty().getId());			
		}
		if (!ArrayUtils.isEmpty(getOfferStatuses())) {
			String status = getController().resolveAlias(ICommercialAlias.OFFER_STATUS);
			addEnumToCriteria(criteria, status, getOfferStatuses());
		}
	}	

}