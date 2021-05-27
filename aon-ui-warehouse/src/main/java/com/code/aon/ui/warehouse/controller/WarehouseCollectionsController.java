package com.code.aon.ui.warehouse.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.code.aon.warehouse.enumeration.InventoryStatus;
import com.code.aon.warehouse.enumeration.PriceType;
import com.esferalia.aon.entity.IEntityAlias;

public class WarehouseCollectionsController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectItem> warehouses;
	private List<SelectItem> deliveryStatuses;
	private List<SelectItem> incomeStatuses;
	private List<SelectItem> priceTypes;
	private List<SelectItem> inventoryStatuses;

	public Warehouse getWarehouse() {
		return null;
	}

	public void setWarehouse( Warehouse warehouse ) {
	}
	
	public int getWarehouseCount() throws ManagerBeanException {
		IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
		Criteria criteria = new Criteria();
		Expression nullWorkPlaceExp = ExpressionUtilities.getNullExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE));
		String ljAlias = StringUtils.replace(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_SCOPE_ID), ".scope", "<scope");
		Expression scopeExp = UserUtils.getInstance().getNullableScopeExpression(ljAlias);
		criteria.addExpression(ExpressionUtilities.getOrExpression(nullWorkPlaceExp, scopeExp));
		criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_ACTIVE), Boolean.TRUE);
		return warehouseBean.getCount(criteria);
	}
	
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		warehouses = new LinkedList<SelectItem>();
		IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
		Criteria criteria = new Criteria();
		Expression nullWorkPlaceExp = ExpressionUtilities.getNullExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE));
		String ljAlias = StringUtils.replace(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_SCOPE_ID), ".scope", "<scope");
		Expression scopeExp = UserUtils.getInstance().getNullableScopeExpression(ljAlias);
		criteria.addExpression(ExpressionUtilities.getOrExpression(nullWorkPlaceExp, scopeExp));
		criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_ACTIVE), Boolean.TRUE);
		criteria.addOrder(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_NAME));
		for (ITransferObject ito : warehouseBean.getList(criteria)) {
			Warehouse warehouse = (Warehouse)ito;
			SelectItem item = new SelectItem(warehouse, warehouse.getName());
			warehouses.add(item);
		}
		return warehouses;
	}
	
	public static List<Warehouse> getWarehouseList(WorkPlace workPlace) throws ManagerBeanException {
		return getWarehouseList(workPlace, false, false);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<Warehouse> getWarehouseList(WorkPlace workPlace, boolean skipScope, boolean skipActive) throws ManagerBeanException {
		IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
		Criteria criteria = new Criteria();
		Expression workPlaceExp = ExpressionUtilities.getNullExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE));
		if (workPlace!=null && workPlace.getId()!=null) {
			Expression workPlaceIdExp = ExpressionUtilities.getEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), workPlace.getId());
			workPlaceExp = ExpressionUtilities.getOrExpression(workPlaceExp, workPlaceIdExp);
		}
		if (!skipScope) {
			String ljAlias = StringUtils.replace(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_SCOPE_ID), ".scope", "<scope");
			Expression scopeExp = UserUtils.getInstance().getNullableScopeExpression(ljAlias);
			criteria.addExpression(ExpressionUtilities.getOrExpression(workPlaceExp, scopeExp));
		} else {
			criteria.addExpression(workPlaceExp);
		}
		if (!skipActive) {
			criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_ACTIVE), Boolean.TRUE);
		}
		criteria.addOrder(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_NAME));
		return (List)warehouseBean.getList(criteria);
	}

	public static List<SelectItem> getWarehouses(WorkPlace workPlace) throws ManagerBeanException {
		return getWarehouses(workPlace, false);
	}
	public static List<SelectItem> getWarehouses(WorkPlace workPlace, boolean skipScope) throws ManagerBeanException {
		List<SelectItem> warehouses = new LinkedList<SelectItem>();
		for (Warehouse warehouse : getWarehouseList(workPlace, skipScope, false)) {
			SelectItem item = new SelectItem(warehouse, warehouse.getName());
			warehouses.add(item);			
		}
		return warehouses;
	}
	
	public List<SelectItem> getDeliveryStatuses() {
		if ( deliveryStatuses == null ) {
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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
			Locale locale = AonUtil.getCurrentLocale();
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

	public List<SelectItem> getInventoryStatuses() {
		if ( inventoryStatuses == null ) {
			Locale locale = AonUtil.getCurrentLocale();
			inventoryStatuses = new LinkedList<SelectItem>();
			for (InventoryStatus status : InventoryStatus.values()) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				inventoryStatuses.add(item);
			}
		}
		return inventoryStatuses;
	}

}