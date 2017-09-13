package com.code.aon.ui.supplier.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.code.aon.ui.registry.controller.event.RegistryPayMethodSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class SupplierSearchListener extends RegistryPayMethodSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private SupplierStatus[] supplierStatuses;
	private Item item;
	
	public SupplierStatus[] getSupplierStatuses() {
		return supplierStatuses;
	}

	public void setSupplierStatuses(SupplierStatus[] supplierStatuses) {
		this.supplierStatuses = supplierStatuses;
	}
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item registryItem) {
		this.item = registryItem;
	}	
	
	@Override
	protected void init() throws ManagerBeanException {
		SupplierStatus[] defaultSupplierStatus = {SupplierStatus.ACTIVE, SupplierStatus.BLOCKED};
		setSupplierStatuses(defaultSupplierStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());		
		super.init();
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getSupplierStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.SUPPLIER_STATUS);
			addEnumToCriteria(criteria, status, getSupplierStatuses());
		}
		if (getItem() != null && getItem().getId() != null) {
			String item = getController().resolveAlias("Registry_items_item_id");
			criteria.addEqualExpression(item, getItem().getId());			
		}
		super.completeCriteria(criteria);
	}
	
}