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
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.code.aon.warehouse.enumeration.PriceType;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseCollectionsController {

	private List<SelectItem> warehouses;
	private List<SelectItem> deliveryStatuses;
	private List<SelectItem> incomeStatuses;
	private List<SelectItem> priceTypes;

	public Warehouse getWarehouse() {
		return null;
	}

	public void setWarehouse( Warehouse warehouse ) {
	}
	
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		warehouses = new LinkedList<SelectItem>();
		IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ACTIVE), true);
		UserUtils.getInstance().addScopeFilterToCriteria(criteria, warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_SCOPE_ID));
		criteria.addOrder(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_NAME));
		List<ITransferObject> c = warehouseBean.getList(criteria);
		Iterator<ITransferObject> iter = c.iterator();
		while (iter.hasNext()) {
			Warehouse warehouse = (Warehouse) iter.next();
			SelectItem item = new SelectItem(warehouse, warehouse.getName());
			warehouses.add(item);
		}
		return warehouses;
	}

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
	
	public List<SelectItem> getPriceTypes() {
		if ( priceTypes == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			priceTypes = new LinkedList<SelectItem>();
			for (PriceType type : PriceType.values()) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name, name, (PriceType.AVERAGE_PURCHASE_PRICE == type));
				priceTypes.add(item);
			}
		}
		return priceTypes;
	}

	public List<SelectItem> getInventories() throws ManagerBeanException {
		List<SelectItem> inventories = new LinkedList<SelectItem>();
		IManagerBean inventoryBean = BeanManager.getManagerBean(Inventory.class);
		try {
			Iterator<?> iter = inventoryBean.getList(null).iterator();
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