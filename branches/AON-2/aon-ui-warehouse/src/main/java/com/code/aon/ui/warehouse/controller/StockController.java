package com.code.aon.ui.warehouse.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class StockController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(StockController.class.getName());

	private static final String BASE_NAME = "com.code.aon.ui.warehouse.i18n.messages";
	
	private Stock movingStock;
	
	private Integer sourceWarehouseId;
	
	private Integer targetWarehouseId;
	
	private double movingQuantity;
	
	private List<SelectItem> availableSourceWarehouses;

	
	public Stock getMovingStock() {
		return movingStock;
	}

	public void setMovingStock(Stock movingStock) {
		this.movingStock = movingStock;
	}

	public Integer getSourceWarehouseId() {
		return sourceWarehouseId;
	}

	public void setSourceWarehouseId(Integer sourceWarehouseId) {
		this.sourceWarehouseId = sourceWarehouseId;
	}

	public Integer getTargetWarehouseId() {
		return targetWarehouseId;
	}

	public void setTargetWarehouseId(Integer targetWarehouseId) {
		this.targetWarehouseId = targetWarehouseId;
	}

	public double getMovingQuantity() {
		return movingQuantity;
	}

	public void setMovingQuantity(double movingQuantity) {
		this.movingQuantity = movingQuantity;
	}

	public void onEditSearch(MenuEvent event){
		super.onEditSearch((ActionEvent)event);
	}
	
	@SuppressWarnings("unchecked")
	public void move(ActionEvent event){
		try {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Criteria criteria = new Criteria();
			Expression itemExp = ExpressionUtilities.getEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), movingStock.getItem().getId());
			criteria.addExpression(itemExp);
			criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), this.getSourceWarehouseId());
			Iterator iter = stockBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				Stock dbSourceStock = (Stock)iter.next();
				if(dbSourceStock.getQuantity() < movingQuantity){
					ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
					AonUtil.addErrorMessage(bundle.getString("stock_impossible_moving"));
					throw new AbortProcessingException(bundle.getString("stock_impossible_moving"));
				}else{
					dbSourceStock.setQuantity(dbSourceStock.getQuantity() - movingQuantity);
					stockBean.update(dbSourceStock);
				}
			}
			criteria = new Criteria();
			criteria.addExpression(itemExp);
			criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), this.getTargetWarehouseId());
			iter = stockBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				Stock dbTargetStock = (Stock)iter.next();
				dbTargetStock.setQuantity(dbTargetStock.getQuantity() + movingQuantity);
				stockBean.update(dbTargetStock);
			}else{
				Stock newTargetStock = new Stock();
				newTargetStock.setItem(movingStock.getItem());
				newTargetStock.setQuantity(movingQuantity);
				newTargetStock.setWarehouse(obtainWarehouse(targetWarehouseId));
				stockBean.insert(newTargetStock);
			}
			this.onSearch(event);
			loadAvailableSourceWarehouses();
			setSourceWarehouseId(null);
			setTargetWarehouseId(null);
			setMovingQuantity(0.0);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	public List<SelectItem> getAvailableSourceWarehouses(){
		return this.availableSourceWarehouses;
	}
	
	@SuppressWarnings("unchecked")
	public void loadAvailableSourceWarehouses()  throws ManagerBeanException{
		this.availableSourceWarehouses = new LinkedList<SelectItem>();
		IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), movingStock.getItem().getId());
		Iterator iter = stockBean.getList(criteria).iterator();
		while(iter.hasNext()){
			Stock stock = (Stock)iter.next();
			SelectItem item = new SelectItem(stock.getWarehouse().getId(), stock.getWarehouse().getName());
			this.availableSourceWarehouses.add(item);
		}
	}
	
	public void addWarehouseExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			this.getCriteria().addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_WAREHOUSE_ID), event.getNewValue());
		}
	}

	public void addItemExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null && !event.getNewValue().toString().trim().equals("")){
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			this.getCriteria().addEqualExpression(stockBean.getFieldName(IWarehouseAlias.STOCK_ITEM_ID), new Integer(event.getNewValue().toString()));
		}
	}
	
	@SuppressWarnings("unchecked")
	private Warehouse obtainWarehouse(Integer targetId) throws ManagerBeanException {
		IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(warehouseBean.getFieldName(IWarehouseAlias.WAREHOUSE_ID), targetId);
		Iterator iter = warehouseBean.getList(criteria,0,1).iterator();
		if(iter.hasNext()){
			return (Warehouse)iter.next();
		}
		return null;
	}
	
	public String onReportByWarehouse() throws ReportException, DAOException{
    	ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("stockWarehouseList");
        manager.setOutputFormat(OutputFormat.PDF);
        String outcome = manager.onExecute();
        return outcome;
    }

	public String onReportByItem() throws ReportException, DAOException, ManagerBeanException{
		this.getCriteria().addOrder(this.getFieldName(IWarehouseAlias.STOCK_ITEM_ID));
    	ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("stockItemList");
        manager.setOutputFormat(OutputFormat.PDF);
        String outcome = manager.onExecute();
        return outcome;
    }
}