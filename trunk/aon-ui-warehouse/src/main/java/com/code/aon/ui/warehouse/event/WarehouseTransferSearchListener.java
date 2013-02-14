package com.code.aon.ui.warehouse.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class WarehouseTransferSearchListener extends RegistrySearchListener {
	
    private Item item;
	
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
	}
}