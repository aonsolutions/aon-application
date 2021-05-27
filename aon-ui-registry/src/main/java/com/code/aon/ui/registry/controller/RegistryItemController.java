package com.code.aon.ui.registry.controller;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryItemController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String type;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public RegistryMode getRegistryMode() {
		return RegistryMode.values()[Integer.parseInt(getType())];
	}

	public boolean isCommercialType() {
		return Integer.parseInt(getType()) == RegistryMode.TARGET.ordinal();
	}

	public boolean isSaleType() {
		return Integer.parseInt(getType()) == RegistryMode.CUSTOMER.ordinal();
	}

	public boolean isPurchaseType() {
		return Integer.parseInt(getType()) == RegistryMode.SUPPLIER.ordinal();
	}

	public boolean isExpenseType() {
		return Integer.parseInt(getType()) == RegistryMode.CREDITOR.ordinal();
	}

	public boolean isMasterItem() {
		return !(getMasterController() instanceof RegistryController);
	}

	public void onItemChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (isPurchaseType()) {
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				RegistryItem rItem = (RegistryItem)getTo();
				rItem.setPriority(calculateNextPriority((Item)event.getNewValue()));
			}
		}
	}	

	public	Integer calculateNextPriority(Item item) throws ManagerBeanException {
		IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), item.getId());
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
		Projection projection = Projection.max(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
		Object value = rItemBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}
