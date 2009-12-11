package com.code.aon.ui.purchase.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class PurchaseSearchListener extends ControllerSearchListener {

	private Supplier supplier;

	private PurchaseStatus[] purchaseStatuses;

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public PurchaseStatus[] getPurchaseStatuses() {
		return purchaseStatuses;
	}

	public void setPurchaseStatuses(PurchaseStatus[] purchaseStatuses) {
		this.purchaseStatuses = purchaseStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setSupplier(new Supplier());
		PurchaseStatus[] defaultPurchaseStatus = {PurchaseStatus.PENDING};
		setPurchaseStatuses(defaultPurchaseStatus);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(IPurchaseAlias.PURCHASE_SUPPLIER_ID), getSupplier().getId());			
		}
		if (!ArrayUtils.isEmpty(getPurchaseStatuses())) {
			String status = getController().resolveAlias(IPurchaseAlias.PURCHASE_STATUS);
			addEnumToCriteria(criteria, status, getPurchaseStatuses());
		}
	}	
}