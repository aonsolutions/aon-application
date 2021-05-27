package com.code.aon.purchase.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;

public class PurchaseDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		PurchaseDetail purchaseDetail = (PurchaseDetail) evt.getTo();
		setDefaultValues(purchaseDetail);
		checkPurchaseDetail(purchaseDetail);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		PurchaseDetail purchaseDetail = (PurchaseDetail) evt.getTo();
		setDefaultValues(purchaseDetail);
		checkPurchaseDetail(purchaseDetail);
	}

	private void setDefaultValues(PurchaseDetail purchaseDetail) {
		if (purchaseDetail.getDiscountExpression() == null || StringUtils.isBlank(purchaseDetail.getDiscountExpression().getDiscountExpr())) {
			purchaseDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
		if (purchaseDetail.getStatus() == null) {
			purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
		}
	}

	private void checkPurchaseDetail(PurchaseDetail purchaseDetail) throws ManagerBeanVetoListenerException {
		if (purchaseDetail.isForcePendingQuantityCancel()) {
			purchaseDetail.setQuantity(purchaseDetail.getDelivered());
			purchaseDetail.setStatus(PurchaseDetailStatus.SETTLED);
		} else if (purchaseDetail.getDelivered() == 0) {
			purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
		} else if ( Math.abs(purchaseDetail.getDelivered()) >= Math.abs(purchaseDetail.getQuantity()) ) {
			purchaseDetail.setQuantity(purchaseDetail.getDelivered());
			purchaseDetail.setStatus(PurchaseDetailStatus.SETTLED);
		} else {
			purchaseDetail.setStatus(PurchaseDetailStatus.PARTIAL_SETTLED);
		}
	}

}