package com.code.aon.purchase.event;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;

public class PurchaseBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Purchase purchase = (Purchase) evt.getTo();
		setDefaultValues(purchase);
		checkPurchase(purchase);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Purchase purchase = (Purchase) evt.getTo();
		setDefaultValues(purchase);
		checkPurchase(purchase);
	}

	private void setDefaultValues(Purchase purchase) {
		if (purchase.getDiscountExpression() == null || StringUtils.isBlank(purchase.getDiscountExpression().getDiscountExpr())) {
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

	private void checkPurchase(Purchase purchase) throws ManagerBeanVetoListenerException {
		int thisYear = CommonUtil.getYear(new Date());
		int purchaseYear = CommonUtil.getYear(purchase.getIssueDate());
		if (purchaseYear < (thisYear-5) || purchaseYear > (thisYear+1)) {
			throw new ManagerBeanVetoListenerException("La Fecha del Pedido no es correcta.");
		}
	}

}