package com.code.aon.ui.warehouse.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ui.form.BasicController;
import com.code.aon.warehouse.Stock;

public class StockController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public void onRemoveStock0( ActionEvent event ) throws ManagerBeanException {
		for( ITransferObject to :  getManagerBean().getList(getCriteria()) ) {
			Stock stock = (Stock) to;
			if ( (stock.getQuantity() == 0)) {
				getManagerBean().remove(stock);
			}
		}
		onSearch(event);
	}
	
	public void onRemoveDiscontinued( ActionEvent event ) throws ManagerBeanException {
		for( ITransferObject to :  getManagerBean().getList(getCriteria()) ) {
			Stock stock = (Stock) to;
			if ( (stock.getQuantity() == 0) && (stock.getItem().getStatus()==ProductStatus.DISCONTINUED)) {
				getManagerBean().remove(stock);
			}
		}
		onSearch(event);
	}
	
	public void onRemoveNoInventoriables( ActionEvent event ) throws ManagerBeanException {
		for( ITransferObject to :  getManagerBean().getList(getCriteria()) ) {
			Stock stock = (Stock) to;
			if ( (stock.getQuantity() == 0) && (!stock.getItem().getProduct().isInventoriable())) {
				getManagerBean().remove(stock);
			}
		}
		onSearch(event);		
	}
}
