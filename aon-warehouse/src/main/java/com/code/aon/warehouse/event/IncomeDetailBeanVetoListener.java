package com.code.aon.warehouse.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.warehouse.IncomeDetail;

public class IncomeDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IncomeDetail incomeDetail = (IncomeDetail) evt.getTo();
		setDefaultValues(incomeDetail);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		IncomeDetail incomeDetail = (IncomeDetail) evt.getTo();
		setDefaultValues(incomeDetail);
	}

	private void setDefaultValues(IncomeDetail incomeDetail) {
		if (incomeDetail.getDiscountExpression() == null || StringUtils.isBlank(incomeDetail.getDiscountExpression().getDiscountExpr())) {
			incomeDetail.setDiscountExpression(new DiscountExpression("0.0"));
		}
	}

}