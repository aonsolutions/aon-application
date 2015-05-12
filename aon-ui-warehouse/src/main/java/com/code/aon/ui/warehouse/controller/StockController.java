package com.code.aon.ui.warehouse.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Brand;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ui.form.BasicController;
import com.code.aon.warehouse.Stock;
import com.code.aon.warehouse.Warehouse;

public class StockController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	
	Warehouse warehouse;
	Integer quantity;
	String code;
	String name;
	String barcode;
	String provider;
	Brand brand;
	
	public String getInitialize(){
		
		warehouse = new Warehouse();
		quantity = null;
		code = "";
		name = "";
		barcode = "";
		provider = "";
		brand = new Brand();
	
		return "";
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	
	
}
