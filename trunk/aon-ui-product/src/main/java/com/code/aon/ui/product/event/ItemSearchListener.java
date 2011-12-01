package com.code.aon.ui.product.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class ItemSearchListener extends RegistrySearchListener {

	private ProductStatus[] itemStatuses;
	private Supplier supplier;
	private Supplier supplierParam;
	
	public ProductStatus[] getItemStatuses() {
		return itemStatuses;
	}

	public void setItemStatuses(ProductStatus[] itemStatuses) {
		this.itemStatuses = itemStatuses;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Supplier getSupplierParam() {
		return supplierParam;
	}

	public void setSupplierParam(Supplier supplierParam) {
		this.supplierParam = supplierParam;
	}

	@Override
	protected void init() throws ManagerBeanException {
		ProductStatus[] defaultItemStatus = {ProductStatus.ACTIVE};
		setItemStatuses(defaultItemStatus);
		IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
		if ((getSupplierParam() != null) && (getSupplierParam().getId() != null)) {
			setSupplier(getSupplierParam());
		} else {
			setSupplier((Supplier)supplierBean.createNewTo());
		}
		setSupplierParam((Supplier)supplierBean.createNewTo());
		super.init();
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getItemStatuses())) {
			String status = getController().resolveAlias(IProductAlias.ITEM_STATUS);
			addEnumToCriteria(criteria, status, getItemStatuses());
		}
		if (getSupplier() != null && getSupplier().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Item_suppliers_supplier_id"), getSupplier().getId());
		}
		super.completeCriteria(criteria);
	}
	
}