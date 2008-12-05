package com.code.aon.ui.warehouse.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.product.util.ItemPriceProvider;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Controller for Inventory.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InventoryController extends BasicController {
	
	/**
	 * The ident of the warehouse
	 */
	private Warehouse warehouse; 
	
	/**
	 * Inicialize stock
	 */
	private boolean initStock;

	/**
	 * Inventory Detail controller name
	 */
	private static final String INVENTORY_DETAIL_CONTROLLER_NAME = "inventoryDetail";
	
	/**
	 * Price provider
	 */
	private ItemPriceProvider provider;
	
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

	/**
	 * Closing inventory event
	 * 
	 * @param event action event
	 * @throws Exception
	 */
	public void onClosing(ActionEvent event) throws Exception {
		closeInventary();
	}
	
	/**
	 * EditSearch event with no category selected 
	 * 
	 * @param event menu event
	 * @throws Exception
	 */
	public void onStartClosing(ActionEvent event) throws Exception {
		this.initStock = false;
		this.warehouse = null;
		super.onReset(event);
	}
	
	/**
	 * Closes the inventary
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void closeInventary() throws Exception{
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try{
			HibernateUtil.beginTransaction();
			
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
			IManagerBean inventoryDetailBean = BeanManager.getManagerBean(InventoryDetail.class);

			if (initStock){
				Iterator initStockListIter = stockBean.getList(null).iterator();
				while (initStockListIter.hasNext()){
					Stock initStock = (Stock) initStockListIter.next();
					initStock.setQuantity(new Double(0));
					stockBean.update(initStock);
				}
			}
			
			Inventory inventory = new Inventory();
			inventory.setInventoryDate(((Inventory)this.getTo()).getInventoryDate());
			inventory.setDescription(((Inventory)this.getTo()).getDescription());
			
			inventory.setWarehouse(warehouse);
			inventory = (Inventory) inventoryBean.insert(inventory);
			
	        Session session = HibernateUtil.getSession();
	        Query q = session.createQuery(
	                "select item " +
	                "from Item as item, Product prod, ProductCategory cat " +
	                "where item.product=prod.id " +
	                "and prod.category=cat.id " +
	                "and prod.inventoriable=true " +
	                " order by prod.category,item.detail");
			Iterator iter = q.list().iterator();
			while (iter.hasNext()){
				InventoryDetail inventoryDetail = new InventoryDetail();
				inventoryDetail.setInventory(inventory);
				Item item = (Item) iter.next();
				inventoryDetail.setItem(item);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID) ,item.getId());
				criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID) ,warehouse.getId());
				List stockList = stockBean.getList(criteria);
				Iterator stockListIter = stockList.iterator();
				int total = 0;
				while (stockListIter.hasNext()){
					Stock stock = (Stock) stockListIter.next();
					total += stock.getQuantity().intValue();
				}
				inventoryDetail.setRealQuantity(total);
				inventoryDetail.setActualQuantity(total);
				inventoryDetail.setCost(getProvider().getInventoryBasePrice(item));
				inventoryDetail = (InventoryDetail) inventoryDetailBean.insert(inventoryDetail);
			}
			HibernateUtil.commitTransaction();
			this.clearCriteria();
			this.addExpression(inventoryBean.getFieldName(IWarehouseAlias.INVENTORY_ID),inventory.getId().toString());
			this.onSearch(null);
			this.getModel().setRowIndex(0);
			this.onSelect(null);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction();
			} catch (DAOException daoe) {
			}
			throw e;
		} finally {
			HibernateUtil.closeSession();
			HibernateUtil.setCloseSession(true);
			HibernateUtil.setBeginTransaction(true);
		}
	}

	/**
	 * Returns a list of Inventories with today date
	 * 
	 * @return list of inventories with today as date
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	private List getTodayList() throws ManagerBeanException{
		IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(inventoryBean.getFieldName(IWarehouseAlias.INVENTORY_INVENTORY_DATE) ,new Date());
		List list = inventoryBean.getList(criteria);
		return list;
	}
	
	/**
	 * If is any inventory with today as date the inventory is allready done
	 * 
	 * @return true if exists a inventory with today as date
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public boolean isInventaryDone() throws ManagerBeanException{
		IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(inventoryBean.getFieldName(IWarehouseAlias.INVENTORY_INVENTORY_DATE) ,new Date());
		return (inventoryBean.getCount(criteria) > 0);
	}
	
	/**
	 * An action called by the menu to load today's inventory
	 * or set the controller to create one
	 * 
	 * @param event the menu event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public void onSearchToday(ActionEvent event) throws ManagerBeanException{
		IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
		List list = getTodayList();
		if (list.size()>0){
			this.clearCriteria();
			this.addExpression(inventoryBean.getFieldName(IWarehouseAlias.INVENTORY_ID),((Inventory)list.get(0)).getId().toString());
			this.onSearch(null);
			this.getModel().setRowIndex(0);
			this.onSelect(null);
		}else{
			super.onReset(null);
			model = new PageDataModel(this,getPageLimit());
		}
		resetTo();
	}

	@Override
	public void onSelect(ActionEvent event) {
		InventoryDetailController idc = (InventoryDetailController)FormUtil.getController(INVENTORY_DETAIL_CONTROLLER_NAME);
		idc.setCategory(null);
		super.onSelect(event);
	}

	/**
	 * Returns the price provider
	 * 
	 * @return price provider
	 */
	public ItemPriceProvider getProvider() {
	    if (provider == null) {
			provider = new ItemPriceProvider();
        }
	    return provider;
    }
}