package com.code.aon.ui.warehouse.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class StockController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(StockController.class.getName());

	private static final String BASE_NAME = "com.code.aon.ui.warehouse.i18n.messages";
	
	private Warehouse sourceWarehouse;
	
	private Warehouse targetWarehouse;
	
	private double movingQuantity;
	
	private double maximumQuantity;
	
	private List<Stock> stocks;
	
	private DataModel stockModel;
	
	private List<SelectItem> availableSourceWarehouses;
	
	private Stock getStock() {
		return (Stock) getTo();
	}
	
	public DataModel getStockModel() {
		return stockModel;
	}

	public void setStockModel(DataModel stockModel) {
		this.stockModel = stockModel;
	}

	public Warehouse getSourceWarehouse() {
		return sourceWarehouse;
	}

	public void setSourceWarehouse(Warehouse sourceWarehouse) {
		this.sourceWarehouse = sourceWarehouse;
	}

	public Warehouse getTargetWarehouse() {
		return targetWarehouse;
	}

	public void setTargetWarehouse(Warehouse targetWarehouse) {
		this.targetWarehouse = targetWarehouse;
	}

	public double getMovingQuantity() {
		return movingQuantity;
	}

	public void setMovingQuantity(double movingQuantity) {
		this.movingQuantity = movingQuantity;
	}
	
	private Stock getStock( Warehouse warehouse ) {
		for( Stock stock : this.stocks ) {
			if ( stock.getWarehouse().equals(warehouse) ) {
				return stock;
			}
		}
		return null;
	}
	
	public double getMaximumQuantity() {
		if ( getSourceWarehouse() != null ) {
			return getStock(getSourceWarehouse()).getQuantity();
		}
		return maximumQuantity;
	}

	public boolean isMoveEnabled() {
		if ( this.maximumQuantity > 0 ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Warehouse.class);
				return bean.getCount(null) > 1;
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public void move(ActionEvent event){
		try {
			IManagerBean stockBean = BeanManager.getManagerBean(Stock.class);
			Stock sourceStock = getStock(getSourceWarehouse());
			if (sourceStock.getQuantity() < movingQuantity) {
				ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
				String message = bundle.getString("warehouse_stock_impossible_moving");
				AonUtil.addErrorMessage( message );
				throw new AbortProcessingException( message );
			} else {
				sourceStock.setQuantity(sourceStock.getQuantity() - movingQuantity);
				stockBean.update(sourceStock);
			}
			Stock targetStock = getStock(getTargetWarehouse());
			if ( targetStock != null ) {
				targetStock.setQuantity(targetStock.getQuantity() + movingQuantity);
				stockBean.update(targetStock);
			}else{
				Stock newTargetStock = new Stock();
				newTargetStock.setItem(getStock().getItem());
				newTargetStock.setQuantity(movingQuantity);
				newTargetStock.setWarehouse(getTargetWarehouse());
				stockBean.insert(newTargetStock);
			}
			initTransferData();
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
	private void updateAvailableSourceWarehouses() {
		this.availableSourceWarehouses = new LinkedList<SelectItem>();
		for( Stock stock : stocks ) {
			SelectItem item = new SelectItem(stock.getWarehouse(), stock.getWarehouse().getName());
			this.availableSourceWarehouses.add(item);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadStockkModel() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getFieldName(IWarehouseAlias.STOCK_ITEM_ID), getStock().getItem().getId());
		this.stocks = (List) getManagerBean().getList(criteria);
		this.maximumQuantity = 0;
		for( Stock stock : stocks ) {
			this.maximumQuantity = Math.max(this.maximumQuantity, stock.getQuantity());
		}
		this.stockModel = new ListDataModel( this.stocks );
	}
	
	public void initTransferData() throws ManagerBeanException {
		setSourceWarehouse(null);
		setTargetWarehouse(null);
		setMovingQuantity(0.0);
		loadStockkModel();
		updateAvailableSourceWarehouses();		
	}
}