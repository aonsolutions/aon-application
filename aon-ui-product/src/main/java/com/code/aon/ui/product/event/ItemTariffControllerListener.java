package com.code.aon.ui.product.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemTariff;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.ItemTariffType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ItemTariffControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)((LinesController)event.getController()).getMasterController().getTo();
		ItemTariff itemTariff = (ItemTariff)event.getController().getTo();
		itemTariff.setItem(item);
		itemTariff.setType(ItemTariffType.FIXED);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Item item = (Item)((LinesController)event.getController()).getMasterController().getTo();
		ItemTariff itemTariff = (ItemTariff)event.getController().getTo();
		itemTariff.setItem(item);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ItemTariff itemTariff = (ItemTariff)event.getController().getTo();
		try {
			IManagerBean itemTariffBean = BeanManager.getManagerBean(ItemTariff.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemTariffBean.getFieldName(IProductAlias.ITEM_TARIFF_ITEM_ID), itemTariff.getItem().getId());
			criteria.addEqualExpression(itemTariffBean.getFieldName(IProductAlias.ITEM_TARIFF_TARIFF_ID), itemTariff.getTariff().getId());
			if (itemTariffBean.getCount(criteria) > 0) {
				throw new ControllerListenerException("El Producto ya esta definido para esa Tarifa.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}