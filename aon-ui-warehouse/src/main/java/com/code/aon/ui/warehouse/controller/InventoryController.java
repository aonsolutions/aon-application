package com.code.aon.ui.warehouse.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller for Inventory.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InventoryController extends BasicController {
	
	private Warehouse warehouse; 

	private boolean initStock;
	private static final String INVENTORY_DETAIL_CONTROLLER_NAME = "inventoryDetail";
	
	public boolean isInitStock() {
		return initStock;
	}
	public void setInitStock(boolean initStock) {
		this.initStock = initStock;
	}
	
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public void onClosing(ActionEvent event) {
		try {
			closeInventary();
		} catch (Exception e) {
			String msg = "Imposible cerrar el inventario. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onStartClosing(ActionEvent event) throws Exception {
		this.initStock = false;
		this.warehouse = null;
		super.onReset(event);
	}
	
	private void closeInventary() throws Exception{
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		String sessionName = HibernateUtil.getSessionFactoryName(); 
		try{
			HibernateUtil.beginTransaction(sessionName);
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
			IManagerBean inventoryDetailBean = BeanManager.getManagerBean(InventoryDetail.class);

	        Session session = HibernateUtil.getSession(sessionName);
			if (initStock){
				Criteria c = new Criteria();
				c.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID), warehouse.getId());
				Iterator<?> initStockListIter = stockBean.getList(c).iterator();
				while (initStockListIter.hasNext()){
					Stock initStock = (Stock) initStockListIter.next();
					initStock.setQuantity(0.0);
					stockBean.update(initStock);
				}
			}
			
			Inventory inventory = new Inventory();
			inventory.setInventoryDate(((Inventory)this.getTo()).getInventoryDate());
			inventory.setDescription(((Inventory)this.getTo()).getDescription());
			
			inventory.setWarehouse(warehouse);
			inventory = (Inventory) inventoryBean.insert(inventory);
			
	        Query q = session.createQuery(
	                "select item " +
	                "from Item as item, Product prod, ProductCategory cat " +
	                "where " + DomainManager.getSQLWhereClause("item.domain") +
	                "and item.product=prod.id " +
	                "and prod.category=cat.id " +
	                "and prod.inventoriable=true " +
	                " order by prod.category,item.detail");
			Iterator<?> iter = q.list().iterator();
			while (iter.hasNext()){
				InventoryDetail inventoryDetail = new InventoryDetail();
				inventoryDetail.setInventory(inventory);
				Item item = (Item) iter.next();
				inventoryDetail.setItem(item);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_ITEM_ID) ,item.getId());
				criteria.addEqualExpression(stockBean.getFieldName(IEntityAlias.STOCK_WAREHOUSE_ID) ,warehouse.getId());
				List<?> stockList = stockBean.getList(criteria);
				Iterator<?> stockListIter = stockList.iterator();
				int total = 0;
				while (stockListIter.hasNext()){
					Stock stock = (Stock) stockListIter.next();
					total += stock.getQuantity().intValue();
				}
				inventoryDetail.setRealQuantity(total);
				inventoryDetail.setActualQuantity(total);
				inventoryDetail.setCost(item.getPurchasePrice());
				inventoryDetail = (InventoryDetail) inventoryDetailBean.insert(inventoryDetail);
			}
			HibernateUtil.commitTransaction(sessionName);
			this.onEditSearch(null);
			getCriteria().addEqualExpression(inventoryBean.getFieldName(IEntityAlias.INVENTORY_ID), inventory.getId());
			this.onSearch(null);
			this.getModel().setRowIndex(0);
			this.onSelect(null);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
			}
			throw e;
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(true);
			HibernateUtil.setBeginTransaction(true);
		}
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		InventoryDetailController idc = (InventoryDetailController)FormUtil.getController(INVENTORY_DETAIL_CONTROLLER_NAME);
		idc.setCategory(null);
		super.onSelect(event);
	}
}


