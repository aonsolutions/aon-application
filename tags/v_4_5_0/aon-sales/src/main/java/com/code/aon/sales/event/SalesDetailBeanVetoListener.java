package com.code.aon.sales.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;

public class SalesDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		SalesDetail salesDetail = (SalesDetail) evt.getTo();
		setDefaultValues(salesDetail);
	}

	private void setDefaultValues(SalesDetail salesDetail) {
		if (salesDetail.getDiscountExpression() == null || salesDetail.getDiscountExpression().getDiscountExpr() == null) {
			salesDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
		if (salesDetail.getStatus() == null) {
			salesDetail.setStatus(SalesDetailStatus.PENDING);
		}
	}

}