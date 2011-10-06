package com.code.aon.purchase.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;

public class PurchaseDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		PurchaseDetail purchaseDetail = (PurchaseDetail) evt.getTo();
		setDefaultValues(purchaseDetail);
		checkPurchaseDetail(purchaseDetail);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		PurchaseDetail purchaseDetail = (PurchaseDetail) evt.getTo();
		checkPurchaseDetail(purchaseDetail);
	}

	private void setDefaultValues(PurchaseDetail purchaseDetail) {
		if (purchaseDetail.getDiscountExpression() == null || purchaseDetail.getDiscountExpression().getDiscountExpr() == null) {
			purchaseDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
		if (purchaseDetail.getStatus() == null) {
			purchaseDetail.setStatus(PurchaseDetailStatus.PENDING);
		}
	}

	private void checkPurchaseDetail(PurchaseDetail purchaseDetail) throws ManagerBeanVetoListenerException {
		if (purchaseDetail.getQuantity() < 0) {
			throw new ManagerBeanVetoListenerException("La Cantidad del Pedido no puede ser negativa.");
		}
		if (purchaseDetail.getQuantity() < purchaseDetail.getDelivered()) {
			purchaseDetail.setQuantity(purchaseDetail.getDelivered());
		}
	}

}