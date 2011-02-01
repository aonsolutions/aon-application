package com.code.aon.sales.event;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;

public class SalesBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Sales sales = (Sales) evt.getTo();
		setDefaultValues(sales);
	}

	private void setDefaultValues(Sales sales) {
		if (sales.getDiscountExpression() == null || sales.getDiscountExpression().getDiscountExpr() == null) {
			sales.setDiscountExpression(new DiscountExpression("0.0"));
		}
		if (sales.getDocumentType() == null) {
			sales.setDocumentType(DocumentType.NORMAL);
		}
		if (sales.getSecurityLevel() == null) {
			sales.setSecurityLevel(SecurityLevel.OFFICIAL);
		}
		if (sales.getStatus() == null) {
			sales.setStatus(SalesStatus.PENDING);
		}
		if (sales.getScope() == null || sales.getScope().getId() == null) {
			sales.setScope(sales.getCustomer().getScope());
		}
	}
}