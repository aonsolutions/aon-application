package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.product.Catalogue;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class CatalogueGadget {

	private List<ITransferObject> list;
	private DataModel model;

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel(getList());
		}
		setList(null);
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<ITransferObject> getList() {
		try {
			if (list == null) {
				IManagerBean bean = BeanManager.getManagerBean(Eccatalogue.class);
				list = bean.getList(null);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		return list;
	}

	public void setList(List<ITransferObject> list) {
		this.list = list;
	}

	public void onSelect(ActionEvent event) {
		try {
			ShopItemsController shop = ECommerceUtil.getShopItems();
			System.out.println();
			shop.resetCriteria(buildItemCriteria());
			shop.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		((ShopController) AonUtil
			.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
			.setItemsView(true);
	}

	private Criteria buildItemCriteria() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(CatalogueItem.class);
			list = bean.getList(null);
			Eccatalogue ecCat = (Eccatalogue) getModel().getRowData();
			String identifier = BeanManager.getManagerBean(Item.class)
					.getFieldName(IECommerceConstants.ITEM_ALIAS);
			Criteria criteria = null;
			for (ITransferObject to : list) {
				CatalogueItem c = (CatalogueItem) to;
				if (c.getCatalogue().getId().equals(ecCat.getId())) {
					if (criteria == null) {
						criteria = new Criteria();
						criteria.addEqualExpression(identifier, c.getItem()
								.getId());
					} else {
						try {
							criteria.addOrExpression(identifier, c.getItem()
									.getId().toString());
						} catch (ExpressionException e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (criteria == null) {
				criteria = new Criteria();
				criteria.addEqualExpression(identifier, null);
			}
			return criteria;
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void paint(OutputStream out, Object data) throws IOException {
		out.write(((Eccatalogue)getList().get(0)).getCatalogueImg());
	}

}
