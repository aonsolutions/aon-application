package com.code.aon.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;

public class ItemBeanListener extends ManagerBeanListenerAdapter {

	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		Item item = (Item)event.getTo();
		if (!item.getProduct().isComposition()) {
			IManagerBean itemCompositionBean = BeanManager.getManagerBean(ItemComposition.class);
			for (ItemComposition composition : item.getItemCompositionList()) {
				itemCompositionBean.remove(composition);
			}
		}
	}

}
