package com.code.aon.purchase.event;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;

public class PurchaseBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Purchase purchase = (Purchase) evt.getTo();
		setDefaultValues(purchase);
	}

	private void setDefaultValues(Purchase purchase) {
		if (purchase.getDiscountExpression() == null || purchase.getDiscountExpression().getDiscountExpr() == null) {
			purchase.setDiscountExpression(new DiscountExpression("0.0"));
		}
		if (purchase.getDocumentType() == null) {
			purchase.setDocumentType(PurchaseDocumentType.NORMAL);
		}
		if (purchase.getSecurityLevel() == null) {
			purchase.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
		if (purchase.getStatus() == null) {
			purchase.setStatus(PurchaseStatus.PENDING);
		}
		if (purchase.getScope() == null || purchase.getScope().getId() == null) {
			purchase.setScope(purchase.getSupplier().getScope());
		}
	}

}