package com.esferalia.aon.gwt.template.shared;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
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
	LinkedList<TemplateInfo> templateList;
	Error error;
	LinkedList<Warehouse> warehouses;
	String warehouseName;
	LinkedList<String> series;
	LinkedList<Series> series2;
	LinkedList<ProductCategory> categories;
	List<Seller> sellerList;
	Boolean closed;
	EcommerceProduct ecommerceProduct;
	LinkedList<String> typeList;
	LinkedList<Tag> tagList;
	Tag tag;
	
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
	public Dialog setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getAccept() {
		return accept;
	}
	public Dialog setAccept(String accept) {
		this.accept = accept;
		return this;
	}
	public Boolean getBoolAccept() {
		return boolAccept;
	}
	public Dialog setBoolAccept(Boolean boolAccept) {
		this.boolAccept = boolAccept;
		return this;
	}
	public String getCancel() {
		return cancel;
	}
	public Dialog setCancel(String cancel) {
		this.cancel = cancel;
		return this;
	}
	public Boolean getBoolCancel() {
		return boolCancel;
	}
	public Dialog setBoolCancel(Boolean boolCancel) {
		this.boolCancel = boolCancel;
		return this;
	}
	public String getType() {
		return type;
	}
	public Dialog setType(String type) {
		this.type = type;
		return this;
	}

	public TemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public Dialog setTemplateInfo(TemplateInfo templateInfo) {
		this.templateInfo = templateInfo;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public Dialog setUrl(String url) {
		this.url = url;
		return this;
	}

	public LinkedList<TemplateInfo> getTemplateList() {
		return templateList;
	}

	public Dialog setTemplateList(LinkedList<TemplateInfo> templateList) {
		this.templateList = templateList;
		return this;
	}

	public Error getError() {
		return error;
	}

	public Dialog setError(Error error) {
		this.error = error;
		return this;
	}

	public LinkedList<Warehouse> getWarehouses() {
		return warehouses;
	}

	public Dialog setWarehouses(LinkedList<Warehouse> warehouses) {
		this.warehouses = warehouses;
		return this;
	}

	public LinkedList<String> getSeries() {
		return series;
	}

	public Dialog setSeries(LinkedList<String> series) {
		this.series = series;
		return this;
	}

	public String getWarehouseName() {
		return warehouseName;
	}

	public Dialog setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
		return this;
	}

	public LinkedList<Series> getSeries2() {
		return series2;
	}

	public Dialog setSeries2(LinkedList<Series> series2) {
		this.series2 = series2;
		return this;
	}

	public Boolean isClosed(){
		return closed;
	}
	
	public Dialog setClosed(Boolean closed){
		this.closed = closed;
		return this;
	}

	public LinkedList<ProductCategory> getCategories() {
		return categories;
	}

	public Dialog setCategories(LinkedList<ProductCategory> categories) {
		this.categories = categories;
		return this;
	}

	public List<Seller> getSellerList() {
		return sellerList;
	}

	public Dialog setSellerList(List<Seller> sellerList) {
		this.sellerList = sellerList;
		return this;
	}

	public EcommerceProduct getEcommerceProduct() {
		return ecommerceProduct;
	}

	public Dialog setEcommerceProduct(EcommerceProduct ecommerceProduct) {
		this.ecommerceProduct = ecommerceProduct;
		return this;
	}

	public LinkedList<String> getTypeList() {
		return typeList;
	}

	public Dialog setTypeList(LinkedList<String> typeList) {
		this.typeList = typeList;
		return this;
	}

	public LinkedList<Tag> getTagList() {
		return tagList;
	}

	public Dialog setTagList(LinkedList<Tag> tagList) {
		this.tagList = tagList;
		return this;
	}

	public Tag getTag() {
		return tag;
	}

	public Dialog setTag(Tag tag) {
		this.tag = tag;
		return this;
	}
	
	

}
