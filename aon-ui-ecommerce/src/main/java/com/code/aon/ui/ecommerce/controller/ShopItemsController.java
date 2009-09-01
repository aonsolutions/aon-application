package com.code.aon.ui.ecommerce.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class ShopItemsController {

	private List<ITransferObject> list;
	private DataModel model;
	private Criteria criteria;

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel(getList());
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<ITransferObject> getList() {
		try {
			if (list == null) {
				IManagerBean bean = BeanManager.getManagerBean(Item.class);
				list = bean.getList(criteria);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void setList(List<ITransferObject> list) {
		this.list = list;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public void resetCriteria(Criteria criteria) throws ManagerBeanException {
		setModel(null);
		setCriteria(criteria);
	}

	public void onSelect(ActionEvent event) {
		((ShopItemController) FormUtil
				.getController(IECommerceConstants.SHOP_ITEM_CONTROLLER))
				.setItem((Item) getModel().getRowData());
		((ShopController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
				.setDetail(true);
		((ShopController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
				.setBackView(IECommerceConstants.ITEM_LIST_VIEW);
	}

	public void onReset(ActionEvent event) {
		try {
			Criteria criteria = null;
			resetCriteria(criteria);
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onSearch(ActionEvent event) {
		setList(null);
		model = new ListDataModel(getList());
	}

	public void addToCart(ActionEvent event) {
		((ShoppingCartController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER))
				.addToCart((Item) model.getRowData());
		((ShopController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
				.setCart(true);
	}

}
