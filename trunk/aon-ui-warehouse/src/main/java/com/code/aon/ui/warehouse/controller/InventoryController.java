package com.code.aon.ui.warehouse.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

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
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.product.util.ItemPriceProvider;
import com.code.aon.ui.util.AonUtil;
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
	private Integer warehouseId; 
	
	/**
	 * The ident of the category
	 */
	private Integer categoryId; 
	
	/**
	 * Inicialize stock
	 */
	private boolean initStock;

	/**
	 * Inventory Detail controller name
	 */
	private static final String INVENTORY_DETAIL_CONTROLLER_NAME = "inventoryDetail";
	
	/**
	 * Init stock True string  
	 */
	private static final String INIT_STOCK_TRUE = "TRUE";
	
	/**
	 * Init stock False string  
	 */
	private static final String INIT_STOCK_FALSE = "FALSE";
	
	/**
	 * Price provider
	 */
	private ItemPriceProvider provider;
	
	/**
	 * Returns false string for init stock
	 * 
	 * @return the iNIT_STOCK_FALSE
	 */
	public static String getINIT_STOCK_FALSE() {
		return INIT_STOCK_FALSE;
	}

	/**
	 * Returns true string for init stock
	 * 
	 * @return the iNIT_STOCK_TRUE
	 */
	public static String getINIT_STOCK_TRUE() {
		return INIT_STOCK_TRUE;
	}
	

	/**
	 * Assigns the warehouse ident
	 * 
	 * @param event the event that contains the warehouse ident
	 * @throws ManagerBeanException
	 */
	public void addWarehouseCriteria(ValueChangeEvent event)
		throws ManagerBeanException {
		if (event.getNewValue() != null) {
			warehouseId = new Integer(event.getNewValue().toString()); 
		}
	}

	/**
	 * Assigns the category ident
	 * 
	 * @param event the event that contains the category ident
	 * @throws ManagerBeanException
	 */
	public void addCategoryCriteria(ValueChangeEvent event)
		throws ManagerBeanException {
		if (event.getNewValue() != null) {
			try{
				categoryId = new Integer(event.getNewValue().toString());
			}catch (Exception e) {
			} 
		}
	}

	/**
	 * Assigns if has to initialice stock
	 * 
	 * @param event contains the value to assign
	 * @throws ManagerBeanException
	 */
	public void hasToInitStock(ValueChangeEvent event)
		throws ManagerBeanException {
		if (event.getNewValue() != null) {
			try{
				if (InventoryController.INIT_STOCK_TRUE.equals(event.getNewValue().toString())){
					this.initStock = true;
				}else{
					this.initStock = false;
				}
			}catch (Exception e) {
			} 
		}
	}

	/**
	 * Closing inventory event
	 * 
	 * @param event action event
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public void onClosing(ActionEvent event) throws Exception {
		closeInventary();
	}
	
	/**
	 * Reset the controller
	 * 
	 * @param event menu event
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws Exception {
		super.onReset(null);
	}
	
	/**
	 * EditSearch event with no category selected 
	 * 
	 * @param event menu event
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public void onEditSearch(MenuEvent event) throws Exception {
		categoryId = null;
		super.onEditSearch(null);
	}

	/**
	 * Closes the inventary
	 * 
	 * @throws Exception
	 */
	private void closeInventary() throws Exception{
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try{
			HibernateUtil.beginTransaction();
			
//			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
			IManagerBean inventoryDetailBean = BeanManager.getManagerBean(InventoryDetail.class);

			if (initStock){
				List initStockList = stockBean.getList(null);
				Iterator initStockListIter = initStockList.iterator();
				while (initStockListIter.hasNext()){
					Stock initStock = (Stock) initStockListIter.next();
					initStock.setQuantity(new Double(0));
					stockBean.update(initStock);
				}
			}
			
			Inventory inventory = new Inventory();
			inventory.setInventoryDate(((Inventory)this.getTo()).getInventoryDate());
			inventory.setDescription(((Inventory)this.getTo()).getDescription());
			
			Criteria warehouseCriteria = new Criteria();
			warehouseCriteria.addEqualExpression(warehouseBean.getFieldName(IWarehouseAlias.WAREHOUSE_ID),warehouseId);
			List warehouseList = warehouseBean.getList(warehouseCriteria);
			Warehouse warehouse = (Warehouse)warehouseList.iterator().next();
			
			inventory.setWarehouse(warehouse);
			inventory = (Inventory) inventoryBean.insert(inventory);
			
			InventoryDetail inventoryDetail;

	        Session session = HibernateUtil.getSession();
	        Query q = session.createQuery(
	                "select item " +
	                "from Item as item, Product prod, ProductCategory cat " +
	                "where item.product=prod.id " +
	                "and prod.category=cat.id " +
	                "and prod.inventoriable=true " +
	                (categoryId==null?"":"and cat.id="+categoryId.intValue())+
	                " order by prod.category,item.detail");
	        List itemList = q.list();
			Iterator iter = itemList.iterator();
			Item item;
			Criteria criteria;
			Stock stock;
			while (iter.hasNext()){
				inventoryDetail = new InventoryDetail();
				inventoryDetail.setInventory(inventory);
				item = (Item) iter.next();
				inventoryDetail.setItem(item);
				criteria = new Criteria();
				criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID) ,item.getId());
				criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID) ,warehouse.getId());
				List stockList = stockBean.getList(criteria);
				Iterator stockListIter = stockList.iterator();
				int total = 0;
				while (stockListIter.hasNext()){
					stock = (Stock) stockListIter.next();
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
	public boolean isInventaryDone() throws ManagerBeanException{
		List list = getTodayList();
		if (list.size()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * An action called by the menu to load today's inventory
	 * or set the controller to create one
	 * 
	 * @param event the menu event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void onSearchToday(MenuEvent event) throws ManagerBeanException{
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

	/**
	 * Resets the inventory detail controller and selects this controlles
	 * 
	 * @param event the action event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void onInitDetailAndSelect(ActionEvent event) throws ManagerBeanException{
		InventoryDetailController idc = (InventoryDetailController)AonUtil.getController(INVENTORY_DETAIL_CONTROLLER_NAME);
		idc.setCategoryId(new Integer(-1));
		this.onSelect(null);
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