package com.code.aon.ui.warehouse.controller;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * Controller for collections
 * 
 * @author Consulting & Development. Joseba Urkiri - 31-may-2006
 * 
 */
public class WarehouseCollectionsController {

	private List<SelectItem> incomeStatuses;

	private List<SelectItem> warehouses;
	
	private List<SelectItem> deliveryStatuses;
	
	/**
	 * Returns a list of income statuses
	 * 
	 * @return IncomeStatus list
	 */
	public List<SelectItem> getIncomeStatuses() {
		if ( incomeStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			incomeStatuses = new LinkedList<SelectItem>();
			for (IncomeStatus type : IncomeStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				incomeStatuses.add(item);
			}
		}
		return incomeStatuses;
	}
	
	public Warehouse getWarehouse() {
		return null;
	}

	public void setWarehouse( Warehouse warehouse ) {
	}
	
	/**
	 * Returns the list of warehouses
	 * 
	 * @return a list of warehouses
	 * @throws ManagerBeanException
	 */
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		if (warehouses == null) {
			warehouses = new LinkedList<SelectItem>();
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(warehouseBean.getFieldName(IWarehouseAlias.WAREHOUSE_NAME));
			List<ITransferObject> c = warehouseBean.getList(criteria);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Warehouse warehouse = (Warehouse) iter.next();
				SelectItem item = new SelectItem(warehouse, warehouse.getName());
				warehouses.add(item);
			}
		}
		return warehouses;
	}

	/**
	 * Returns a list with the delivery statuses
	 * 
	 * @return list of DeliveryStatus
	 */
	public List<SelectItem> getDeliveryStatuses() {
		if ( deliveryStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			deliveryStatuses = new LinkedList<SelectItem>();
			for (DeliveryStatus type : DeliveryStatus.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				deliveryStatuses.add(item);
			}
		}
		return deliveryStatuses;
	}
	
	/**
	 * Return a list of all inventories
	 * 
	 * @return Inventory list
	 * @throws ManagerBeanException 
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getInventories() throws ManagerBeanException {
		List<SelectItem> inventories = new LinkedList<SelectItem>();
		IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
		try {
			Iterator iter = inventoryBean.getList(null).iterator();
			while(iter.hasNext()){
				Inventory inventory = (Inventory)iter.next();
				String date = new SimpleDateFormat("dd/MM/yyyy").format(inventory.getInventoryDate());
				SelectItem item = new SelectItem(date, inventory.getDescription() + " [" + date + "]");
				inventories.add(item);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return inventories;
	}
}