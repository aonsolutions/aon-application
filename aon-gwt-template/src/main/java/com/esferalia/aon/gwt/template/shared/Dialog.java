package com.esferalia.aon.gwt.template.shared;

import java.util.List;
import java.util.Vector;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Dialog implements IsSerializable{
	
	String title;
	String accept;
	Boolean boolAccept;
	String cancel;
	Boolean boolCancel;
	String type;
	TemplateInfo templateInfo;
	String url;
	TemplateList templateList;
	Error error;
	Vector<Warehouse> warehouses;
	String warehouseName;
	Vector<String> series;
	Vector<Series> series2;
	List<ProductCategory> categories;
	List<Seller> sellerList;
	Boolean closed;
	public Dialog() {
	
	}
	
	public Dialog(	String title, String accept,
				Boolean boolAccept, String cancel,
				Boolean boolCancel, String type) {
		this.title = title;
		this.accept = accept;
		this.boolAccept = boolAccept;
		this.cancel = cancel;
		this.boolCancel = boolCancel;
		this.type = type;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public Boolean getBoolAccept() {
		return boolAccept;
	}
	public void setBoolAccept(Boolean boolAccept) {
		this.boolAccept = boolAccept;
	}
	public String getCancel() {
		return cancel;
	}
	public void setCancel(String cancel) {
		this.cancel = cancel;
	}
	public Boolean getBoolCancel() {
		return boolCancel;
	}
	public void setBoolCancel(Boolean boolCancel) {
		this.boolCancel = boolCancel;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public TemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(TemplateInfo templateInfo) {
		this.templateInfo = templateInfo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public TemplateList getTemplateList() {
		return templateList;
	}

	public void setTemplateList(TemplateList templateList) {
		this.templateList = templateList;
	}

	public Error getError() {
		return error;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public Vector<Warehouse> getWarehouses() {
		return warehouses;
	}

	public void setWarehouses(Vector<Warehouse> warehouses) {
		this.warehouses = warehouses;
	}

	public Vector<String> getSeries() {
		return series;
	}

	public void setSeries(Vector<String> series) {
		this.series = series;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}

	public Vector<Series> getSeries2() {
		return series2;
	}

	public void setSeries2(Vector<Series> series2) {
		this.series2 = series2;
	}

	public Boolean isClosed(){
		return closed;
	}
	
	public void setClosed(Boolean closed){
		this.closed = closed;
	}

	public List<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<ProductCategory> categories) {
		this.categories = categories;
	}

	public List<Seller> getSellerList() {
		return sellerList;
	}

	public void setSellerList(List<Seller> sellerList) {
		this.sellerList = sellerList;
	}

}
