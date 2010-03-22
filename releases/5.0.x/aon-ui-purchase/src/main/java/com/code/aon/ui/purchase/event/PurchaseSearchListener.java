package com.code.aon.ui.purchase.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class PurchaseSearchListener extends RegistrySearchListener {
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Purchase_supplier_registry_";

	private Supplier supplier;

	private PurchaseStatus[] purchaseStatuses;
	
	private Item item;
	
	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}		

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
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setSupplier(new Supplier());
		PurchaseStatus[] defaultPurchaseStatus = {PurchaseStatus.PENDING};
		setPurchaseStatuses(defaultPurchaseStatus);
		setItem(new Item());
		getItem().setProduct(new Product());				
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		super.completeCriteria();		
		Criteria criteria = getController().getCriteria();
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(IPurchaseAlias.PURCHASE_SUPPLIER_ID), getSupplier().getId());			
		}
		if (!ArrayUtils.isEmpty(getPurchaseStatuses())) {
			String status = getController().resolveAlias(IPurchaseAlias.PURCHASE_STATUS);
			addEnumToCriteria(criteria, status, getPurchaseStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Purchase.lines.item.id", getItem().getId());
		}				
	}	

}