package com.code.aon.warehouse.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.warehouse.IncomeDetail;

public class IncomeDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IncomeDetail incomeDetail = (IncomeDetail) evt.getTo();
		setDefaultValues(incomeDetail);
	}

	private void setDefaultValues(IncomeDetail incomeDetail) {
		if (incomeDetail.getDiscountExpression() == null || incomeDetail.getDiscountExpression().getDiscountExpr() == null) {
			incomeDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
	}

}