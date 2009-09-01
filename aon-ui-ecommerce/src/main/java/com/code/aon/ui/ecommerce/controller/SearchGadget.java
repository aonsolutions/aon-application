package com.code.aon.ui.ecommerce.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.ecommerce.controller.ShopItemsController;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class SearchGadget {

	private String search;

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public void onSearch(ActionEvent event) throws ManagerBeanException,
			ExpressionException {
		String identifier = BeanManager.getManagerBean(Item.class)
				.getFieldName(IECommerceConstants.ITEM_NAME_ALIAS);
		Criteria c = new Criteria();
		if (getSearch() != null && !getSearch().equals("")) {
			c.addExpression(identifier, "*" + getSearch() + "*");
		}
		search(c);
		setSearch(null);
		((ShopController) AonUtil
			.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
			.setItems(true);
	}

	public void search(Criteria criteria) {
		try {
			ShopItemsController shop = ECommerceUtil.getShopItems();
			shop.resetCriteria(criteria);
			shop.onSearch(null);
			System.out.println();
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
}
