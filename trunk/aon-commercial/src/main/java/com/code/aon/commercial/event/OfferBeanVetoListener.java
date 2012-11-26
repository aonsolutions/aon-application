package com.code.aon.commercial.event;

import java.util.Date;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.util.DiscountExpression;

public class OfferBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Offer offer = (Offer) evt.getTo();
		setDefaultValues(offer);
		checkOffer(offer);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Offer offer = (Offer) evt.getTo();
		checkOffer(offer);
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
		if (offer.getScope() == null || offer.getScope().getId() == null) {
			offer.setScope(offer.getTarget().getScope());
		}
	}

	private void checkOffer(Offer offer) throws ManagerBeanVetoListenerException {
		if (OfferType.DEALERSHIP != offer.getType()) {
			offer.setSupplier(null);
		}

		int thisYear = CommonUtil.getYear(new Date());
		int offerYear = CommonUtil.getYear(offer.getIssueDate());
		if (offerYear < (thisYear-5) || offerYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("La Fecha del Presupuesto no es correcta.");
		}
	}

}