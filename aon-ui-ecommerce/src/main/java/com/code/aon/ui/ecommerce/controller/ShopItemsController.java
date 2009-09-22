package com.code.aon.ui.ecommerce.controller;

import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class ShopItemsController {

	private List<ShopItem> list;
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

	public List<ShopItem> getList() {
		if (list == null) {
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);

				ConfigController cc = (ConfigController) AonUtil
						.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER);
				Tariff tariff = cc.getActiveConfig().getTariff();
				IManagerBean bean = BeanManager.getManagerBean(Item.class);
				List<ITransferObject> itemList = bean.getList(getCriteria());
				setList(new LinkedList<ShopItem>());
				for (ITransferObject to : itemList) {
					Item item = (Item) to;
					ShopItem si = new ShopItem(item, tariff);
					getList().add(si);
				}
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					// nothing
				}
				String msg = "La obtencion de datos falló";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
			}
		}
		return list;
	}

	public void setList(List<ShopItem> list) {
		this.list = list;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public void resetCriteria(Criteria criteria) {
		setModel(null);
		setCriteria(criteria);
	}

	public void onReset(ActionEvent event) {
		Criteria criteria = null;
		resetCriteria(criteria);
	}

	public void onSearch(ActionEvent event) {
		setList(null);
		model = new ListDataModel(getList());
	}

	public void onSelect(ActionEvent event) {
		ShopItemController sic = (ShopItemController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_ITEM_CONTROLLER);
		ShopItem item = (ShopItem) getModel().getRowData();
		sic.setItem(item);
		ShopController sc = (ShopController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView(ViewEnum.ITEM_LIST);
		sc.setContentView(ViewEnum.ITEM_DETAIL);
	}

	public void addToCart(ActionEvent event) {
		((ShoppingCartController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER))
				.addToCart((ShopItem) model.getRowData());
		ShopController sc = (ShopController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView(ViewEnum.ITEM_LIST);
		sc.setContentView(ViewEnum.SHOPPING_CART);
	}

	public void paintThumbnail(OutputStream out, Object data) {
		Integer id = (Integer) data;
		for (ShopItem shopItem : getList()) {
			if (id.equals(shopItem.getId())) {
				shopItem.paintThumbnail(out, data);
			}
		}
	}
}
