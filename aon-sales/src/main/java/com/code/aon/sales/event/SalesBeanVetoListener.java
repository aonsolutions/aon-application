package com.code.aon.sales.event;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.Sales;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;

public class SalesBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Sales sales = (Sales) evt.getTo();
		setDefaultValues(sales);
		checkSales(sales);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Sales sales = (Sales) evt.getTo();
		setDefaultValues(sales);
		checkSales(sales);
	}

	private void setDefaultValues(Sales sales) {
		if (sales.getDiscountExpression() == null || StringUtils.isBlank(sales.getDiscountExpression().getDiscountExpr())) {
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

	private void checkSales(Sales sales) throws ManagerBeanVetoListenerException {
		int thisYear = CommonUtil.getYear(new Date());
		int salesYear = CommonUtil.getYear(sales.getIssueDate());
		if (salesYear < (thisYear-5) || salesYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("La Fecha del Pedido no es correcta.");
		}
	}

}