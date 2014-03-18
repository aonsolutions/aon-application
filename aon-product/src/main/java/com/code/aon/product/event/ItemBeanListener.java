package com.code.aon.product.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.ItemTariff;
import com.code.aon.product.enumeration.ItemTariffType;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		Item item = (Item)event.getTo();
		if (!item.getProduct().isComposition()) {
			IManagerBean itemCompositionBean = BeanManager.getManagerBean(ItemComposition.class);
			for (ItemComposition composition : item.getItemCompositionList()) {
				itemCompositionBean.remove(composition);
			}
		}

		ItemPricesManager pricesManager = new ItemPricesManager();
		IManagerBean itemTariffBean = BeanManager.getManagerBean(ItemTariff.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_ITEM_ID), item.getId());
		criteria.addNotEqualExpression(itemTariffBean.getFieldName(IEntityAlias.ITEM_TARIFF_TYPE), ItemTariffType.FIXED);
		for (ITransferObject ito : itemTariffBean.getList(criteria)) {
			ItemTariff itemTariff = (ItemTariff)ito;
			pricesManager.onProfitChanged(itemTariff, new Double(itemTariff.getProfitPercent()));
			itemTariffBean.update(itemTariff);
		}
	}

}
