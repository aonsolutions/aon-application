package com.code.aon.ui.warehouse.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Inventory;
import com.code.aon.warehouse.InventoryDetail;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.WarehouseTransfer;
import com.code.aon.warehouse.WarehouseTransferDetail;
import com.code.aon.warehouse.enumeration.InventoryStatus;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller for Inventory.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class InventoryController extends BasicController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class.getName());
	
	private Warehouse warehouse; 
	private boolean initStock;
	private boolean showInventoryAdjustmentWindow;
	private boolean showAuditInfoWindow;

	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public boolean isInitStock() {
		return initStock;
	}
	public void setInitStock(boolean initStock) {
		this.initStock = initStock;
	}
	
	public boolean isShowInventoryAdjustmentWindow() {
		return showInventoryAdjustmentWindow;
	}
	public void setShowInventoryAdjustmentWindow(boolean showInventoryAdjustmentWindow) {
		this.showInventoryAdjustmentWindow = showInventoryAdjustmentWindow;
	}
	
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}
	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public void onClosing(ActionEvent event) {
		dateValidation();
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
			inventory.setStatus(InventoryStatus.OPEN);
			inventory.setInventoryDate(((Inventory)this.getTo()).getInventoryDate());
			inventory.setDescription(((Inventory)this.getTo()).getDescription());
			
			inventory.setWarehouse(warehouse);
			inventory = (Inventory) inventoryBean.insert(inventory);
			
	        Query q = session.createQuery(
	                " select item, sum(stock.quantity), item.id " +
	                " from Item as item, Stock as stock " +
	                " where stock.item=item.id " +
	                " and stock.warehouse=" + warehouse.getId() +
	                " and " + DomainManager.getSQLWhereClause("stock.domain") +
	                " group by item.id " +
	                " order by item.detail");
			Iterator<?> iter = q.list().iterator();
			while (iter.hasNext()){
				InventoryDetail inventoryDetail = new InventoryDetail();
				inventoryDetail.setInventory(inventory);
				Object[] o = (Object[]) iter.next();
				Item item = (Item) o[0];
				Double total = (Double) o[1];
				inventoryDetail.setItem(item);
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

	private void dateValidation() {
		Date date = ((Inventory)this.getTo()).getInventoryDate();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Inventory.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_WAREHOUSE_ID), getWarehouse().getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_INVENTORY_DATE), date);
			if ( bean.getCount(criteria) > 0 ) {
				String message = AonUtil.getMessage(ICommonMessages.WAREHOUSE_INVENTORY_DATE_ERROR);
				AonUtil.addErrorMessage(message);
				throw new AbortProcessingException(message);					
			}
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<InventoryDetail> getDetails( Inventory inventory, boolean newElements ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(InventoryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVENTORY_DETAIL_INVENTORY_ID), inventory.getId());
		String actualAlias = bean.getFieldName(IEntityAlias.INVENTORY_DETAIL_ACTUAL_QUANTITY);
		Expression expr1 = ExpressionUtilities.getIdentifierExpression(actualAlias);
		String realAlias = bean.getFieldName(IEntityAlias.INVENTORY_DETAIL_REAL_QUANTITY);
		if ( newElements ) {
			criteria.addExpression(ExpressionUtilities.getGreaterThanExpression(realAlias, expr1));
		} else {
			criteria.addExpression(ExpressionUtilities.getLessThanExpression(realAlias, expr1));
		}
		return (List) bean.getList(criteria);
	}
	
	private WarehouseTransfer getWarehouseTransfer( Inventory inventory, boolean newElements ) throws ManagerBeanException {
		WarehouseTransferController wtc = (WarehouseTransferController) AonUtil.getRegisteredBean(IWarehouseConstants.WAREHOUSE_TRANSFER_CONTROLLER_NAME);
		WarehouseTransfer _wt = (WarehouseTransfer) wtc.getTo();
		WarehouseTransfer wt = new WarehouseTransfer();
		wt.setSeries(_wt.getSeries());
		if ( _wt.getNumber() == 0 ) {
			_wt.setNumber(wtc.obtainMaxNumber(_wt.getSeries()));
		}
		wt.setNumber(_wt.getNumber());
		wt.setSecurityLevel(_wt.getSecurityLevel());
		wt.setInventory(inventory);
		wt.setIssueTime(inventory.getInventoryDate());
		if ( newElements ) {
			wt.setTargetWarehouse(inventory.getWarehouse());
		} else {
			wt.setSourceWarehouse(inventory.getWarehouse());
		}
		IManagerBean bean = BeanManager.getManagerBean(WarehouseTransfer.class);
		bean.insert(wt);
		_wt.setNumber(wtc.obtainMaxNumber(_wt.getSeries()));		
		return wt;
	}
	
	private void createWarehouseTransfer( Inventory inventory, boolean newElements ) throws ManagerBeanException {
		List<InventoryDetail> details = getDetails(inventory, newElements);
		if (! details.isEmpty() ) {
			WarehouseTransfer wt = getWarehouseTransfer(inventory, newElements);
			IManagerBean bean = BeanManager.getManagerBean(WarehouseTransferDetail.class);
			for( InventoryDetail detail : details ) {
				WarehouseTransferDetail wtd = new WarehouseTransferDetail();
				wtd.setWarehouseTransfer(wt);
				wtd.setItem(detail.getItem());
				double quantity = Math.abs(detail.getActualQuantity()-detail.getRealQuantity());
				wtd.setQuantity(quantity);
				bean.insert(wtd);
			}
		}		
	}
	
	public void onStartAdjustment(ActionEvent event) {
		setShowInventoryAdjustmentWindow(true);
		IController controller = FormUtil.getController(IWarehouseConstants.WAREHOUSE_TRANSFER_CONTROLLER_NAME);
		controller.onReset(event);
	}
	
	public void onAdjustment(ActionEvent event) {
		Inventory inventory = (Inventory) getTo();
		try {
			createWarehouseTransfer(inventory, true);
			createWarehouseTransfer(inventory, false);
			inventory.setStatus(InventoryStatus.PROCESSED);
			getManagerBean().update(inventory);
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
}