package com.code.aon.commercial.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;

public class OfferDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		OfferDetail offerDetail = (OfferDetail) evt.getTo();
		setDefaultValues(offerDetail);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		OfferDetail offerDetail = (OfferDetail) evt.getTo();
		setDefaultValues(offerDetail);
	}

	private void setDefaultValues(OfferDetail offerDetail) {
		if (offerDetail.getDiscountExpression() == null || StringUtils.isBlank(offerDetail.getDiscountExpression().getDiscountExpr())) {
			offerDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
	}

}