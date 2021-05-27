package com.code.aon.ui.warehouse.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class ConsumptionControlController implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String INCOME_DETAIL_INCOME_ISSUE_TIME = "IncomeDetail.income.issueTime";

	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryController.class);
	
	private List<SelectItem> warehouses;
	private List<SelectItem> beforeInventories;
	private List<SelectItem> afterInventories;
	
	private Warehouse warehouse;
	private List<Inventory> inventories;
	private Inventory inventoryBefore;
	private Inventory inventoryAfter;
	private boolean printHeader;
	private boolean errorControl;
	private boolean detail;
	
	public Warehouse getWarehouse() {
		return warehouse;
	}
	
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	public Inventory getInventoryBefore() {
		return inventoryBefore;
	}

	public void setInventoryBefore(Inventory inventoryBefore) {
		this.inventoryBefore = inventoryBefore;
	}

	public Inventory getInventoryAfter() {
		return inventoryAfter;
	}

	public void setInventoryAfter(Inventory inventoryAfter) {
		this.inventoryAfter = inventoryAfter;
	}

	public List<SelectItem> getWarehouses() {
		return this.warehouses;
	}
	
	public List<SelectItem> getBeforeInventories() {
		return beforeInventories;
	}

	public List<SelectItem> getAfterInventories() {
		return afterInventories;
	}

	public String getBeanName() {
		return "consumptionControl";
	}

	public void onEditSearch(ActionEvent event) {
		setWarehouse(null);
		setInventoryBefore(null);
		setInventoryAfter(null);
		this.beforeInventories = new LinkedList<SelectItem>();
		this.afterInventories = new LinkedList<SelectItem>();
		try {
			loadWarehouses();	
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void onWarehouseChanged(ActionEvent event) {
		try {
			inventories = getInventories(getWarehouse());
			beforeInventories = getList(inventories, 1, inventories.size());
			afterInventories = getList(inventories, 0, inventories.size()-1);
			if ( errorControl || (inventories.size() == 2) ) {
				setInventoryAfter(inventories.get(0));
				setInventoryBefore(inventories.get(1));
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	public void onInventoryBeforeChanged(ActionEvent event) {
		int index = Math.max(inventories.indexOf(inventoryBefore), inventories.size());
		afterInventories = getList(inventories, 0, index);
		if ( afterInventories.size() == 1 ) {
			Inventory inventory = (Inventory) afterInventories.get(0).getValue();
			setInventoryAfter(inventory);
		}
	}
	
	public void onDetailChanged(ActionEvent event) {
		System.out.println(detail); 
	}
	
	public void onInventoryAfterChanged(ActionEvent event) {
		int index = Math.max(inventories.indexOf(inventoryAfter), 0);
		beforeInventories = getList(inventories, index+1, inventories.size());
		if ( beforeInventories.size() == 1 ) {
			Inventory inventory = (Inventory) beforeInventories.get(0).getValue();
			setInventoryBefore(inventory);
		} else if ( inventories.indexOf(inventoryBefore) < (index+1) ) {
			setInventoryBefore(null);
		}		
	}
	
	public List<SelectItem> getList( List<Inventory> inventories, int start, int end ) {
		List<SelectItem> list = new LinkedList<SelectItem>();
		Locale locale = AonUtil.getCurrentLocale();
		String pattern = AonUtil.getMessage(ICommonMessages.DATE_PATTERN);
		for( int i = start; i < end; i++ ) {
			Inventory inventory = inventories.get(i);
			String date = DateFormatUtils.format(inventory.getInventoryDate(), pattern, locale); 
			String description = inventory.getDescription() + " (" + date + ")";
			list.add( new SelectItem(inventory, description) );
		}
		return list;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Inventory> getInventories( Warehouse warehouse ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Inventory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_WAREHOUSE_ID), warehouse.getId());
		criteria.addOrder(bean.getFieldName(IEntityAlias.INVENTORY_INVENTORY_DATE), false);
		return (List) bean.getList(criteria);
	}
	
	private boolean hasInventories( Warehouse warehouse ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Inventory.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_WAREHOUSE_ID), warehouse.getId());
		return bean.getCount(criteria) > 1;
	}
	
	private void loadWarehouses() throws ManagerBeanException {
		this.warehouses = new LinkedList<SelectItem>();
		WarehouseCollectionsController wcc = (WarehouseCollectionsController) AonUtil.getRegisteredBean(IWarehouseConstants.COLLECTIONS_CONTROLLER_NAME);
		for( SelectItem item : wcc.getWarehouses() ) {
			Warehouse warehouse = (Warehouse) item.getValue();
			if ( hasInventories(warehouse) ) {
				this.warehouses.add(item);
			}
		}
	}
	
	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	private double getIncomeQuantity( Item item ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INCOME_DETAIL_WAREHOUSE_ID), getWarehouse().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INCOME_DETAIL_ITEM_ID), item.getId());
		criteria.addGreaterThanOrEqualExpression(INCOME_DETAIL_INCOME_ISSUE_TIME, inventoryBefore.getInventoryDate());
		criteria.addLessThanOrEqualExpression(INCOME_DETAIL_INCOME_ISSUE_TIME, inventoryAfter.getInventoryDate());
		Double result = (Double) bean.getUniqueResult(Projection.sum(bean.getFieldName(IEntityAlias.INCOME_DETAIL_QUANTITY)), criteria);
		return (result != null) ? result : 0.0;
	}
	
	private void fill( Inventory inventory, boolean before, Map<ConsumptionKey,Consumption> map ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(InventoryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_DETAIL_INVENTORY_ID), inventory.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			InventoryDetail id = (InventoryDetail) to;
			ConsumptionKey key = ConsumptionKey.get(id.getItem());
			Consumption consumption = map.get(key); 
			if ( consumption == null ) {
				consumption = new Consumption(id.getItem());
				consumption.setIncomeQuantity(getIncomeQuantity(id.getItem()));
				map.put(key, consumption);
			}
			consumption.addQuantity(id.getRealQuantity(), before);
		}		
	}
	
	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		Map<ConsumptionKey,Consumption> map = new TreeMap<ConsumptionKey,Consumption>();
		fill(inventoryBefore, true, map);
		fill(inventoryAfter, false, map);
		if ( errorControl ) {
			List<Consumption> list = new LinkedList<Consumption>();
			for( Consumption consumption : map.values() ) {
				if ( consumption.getConsumption() < 0 ) {
					list.add(consumption);
				}
			}
			return list;
		}
		return map.values();
	}
	
	public boolean isPrintHeader() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return controller.isPrintHeader() && this.printHeader;
	}

	public void setPrintHeader(boolean printHeader) {
		this.printHeader = printHeader;
	}
	
	public boolean isErrorControl() {
		return errorControl;
	}

	public void setErrorControl(boolean errorControl) {
		this.errorControl = errorControl;
	}
	
	/*public boolean isDetail() {
		return detail;
	}*/
	
	public boolean getDetail() {
		return detail;
	}

	public void setDetail(boolean detail) {
		this.detail = detail;
	}

	public static class Consumption {
		
		private Item item;
		private double beforeQuantity;
		private double incomeQuantity;
		private double afterQuantity;
		
		public Consumption(Item item) {
			this.item = item;
		}

		public Item getItem() {
			return item;
		}

		public void setItem(Item item) {
			this.item = item;
		}

		public void addQuantity( double quantity, boolean before ) {
			if ( before ) {
				this.beforeQuantity += quantity;
			} else {
				this.afterQuantity += quantity;
			}
		}
		
		public double getBeforeQuantity() {
			return beforeQuantity;
		}

		public double getIncomeQuantity() {
			return incomeQuantity;
		}

		public void setIncomeQuantity(double incomeQuantity) {
			this.incomeQuantity = incomeQuantity;
		}

		public double getAfterQuantity() {
			return afterQuantity;
		}
		
		public double getConsumption() {
			return (beforeQuantity+incomeQuantity)-afterQuantity;
		}
		
	}
	
	private static class ConsumptionKey implements Comparable<ConsumptionKey> {
		
		private Integer id;
		
		private String code;

		private ConsumptionKey(Integer id, String code) {
			this.id = id;
			this.code = code;
		}
		
		public static ConsumptionKey get( Item item ) {
			return new ConsumptionKey(item.getId(), item.getProduct().getCode());
		}

		@Override
		public int compareTo(ConsumptionKey o) {
			if ( StringUtils.equals(code, o.code) ) {
				return id.compareTo(o.id);
			}
			return code.compareTo(o.code);
		}
		
	}

}
