package com.code.aon.commercial.event;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;

public class OfferBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Offer offer = (Offer) evt.getTo();
		setDefaultValues(offer);
	}

	private void setDefaultValues(Offer offer) {
		if (offer.getDiscountExpression() == null || offer.getDiscountExpression().getDiscountExpr() == null) {
			offer.setDiscountExpression(new DiscountExpression("0.0"));
		}
		if (offer.getType() == null) {
			offer.setType(OfferType.NORMAL);
		}
		if (offer.getSecurityLevel() == null) {
			offer.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
		if (offer.getStatus() == null) {
			offer.setStatus(OfferStatus.PENDING);
		}
	}
}