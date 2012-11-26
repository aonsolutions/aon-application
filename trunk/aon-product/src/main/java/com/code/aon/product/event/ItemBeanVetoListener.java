package com.code.aon.product.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.product.Item;

public class ItemBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Item item = (Item)evt.getTo();
		checkItem(item);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Item item = (Item)evt.getTo();
		checkItem(item);
	}

	private void checkItem(Item item) {
    	if (StringUtils.isEmpty(item.getProduct().getCode())) {
    		item.getProduct().setCode(item.getId().toString());
    	}
    	if (item.getProduct().isInventoriable()) {
    		item.getProduct().setComposition(false);
    	}
    	if (!item.getProduct().isComposition()) {
    		item.getProduct().setCompositionPrice(false);
    	}
	}

}