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
import com.code.aon.product.ProductCategory;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class CategoryGadget {

	private List<ITransferObject> list;
	private DataModel model;

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
				IManagerBean bean = BeanManager
						.getManagerBean(ProductCategory.class);
				list = bean.getList(null);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void setList(List<ITransferObject> list) {
		this.list = list;
	}

	public void onSelect(ActionEvent event) {
		try {
			ProductCategory cat = (ProductCategory) getModel().getRowData();
			Criteria criteria = new Criteria();
			String identifier = BeanManager.getManagerBean(Item.class)
					.getFieldName(IECommerceConstants.CATEGORY_ALIAS);
			criteria.addEqualExpression(identifier, cat.getId());
			/*
			 * AINADIR AL CRITERIA EL internetVisible DE ITEM A true
			 * 
			 */
			ShopItemsController shop = ECommerceUtil.getShopItems();
			shop.resetCriteria(criteria);
			shop.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView( sc.getContentView() );
		sc.setContentView( ViewEnum.ITEM_LIST );
	}

}
