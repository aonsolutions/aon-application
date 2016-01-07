package com.esferalia.aon.gwt.template.server;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StockInfo implements IsSerializable{
	
	String product;
	Integer productId;
	//Series series;
	//Warehouse sourceWarehouse;
	//Warehouse targetWarehouse;
	String detail;
	String detail2;
	String detail3;
	//String comments;
	Double quantity;
	Integer row;
	Integer itemId;
	Integer domainId;
	Integer transferId;
	Double quantityDifference;
	String productName;
	String workplaceStr;
	String departmentStr;
	String serialNumber;
	
	
	public String getWorkplaceStr() {
		return workplaceStr;
	}
	public StockInfo setWorkplaceStr(String workplaceStr) {
		this.workplaceStr = workplaceStr;
		return this;
	}
	public String getDepartmentStr() {
		return departmentStr;
	}
	public StockInfo setDepartmentStr(String departmentStr) {
		this.departmentStr = departmentStr;
		return this;
	}
	public String getProduct() {
		return product;
	}
	public StockInfo setProduct(String product) {
		this.product = product;
		return this;
	}
	public String getDetail() {
		return detail;
	}
	public StockInfo setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	public String getDetail2() {
		return detail2;
	}
	public StockInfo setDetail2(String detail2) {
		this.detail2 = detail2;
		return this;
	}
	public String getDetail3() {
		return detail3;
	}
	public StockInfo setDetail3(String detail3) {
		this.detail3 = detail3;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public StockInfo setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public Integer getProductId() {
		return productId;
	}
	public StockInfo setProductId(Integer productId) {
		this.productId = productId;
		return this;
	}
	public Integer getRow() {
		return row;
	}
	public StockInfo setRow(Integer row) {
		this.row = row;
		return this;
	}
	public Integer getItemId() {
		return itemId;
	}
	public StockInfo setItemId(Integer itemId) {
		this.itemId = itemId;
		return this;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public StockInfo setDomainId(Integer domainId) {
		this.domainId = domainId;
		return this;
	}
	public Integer getTransferId() {
		return transferId;
	}
	public StockInfo setTransferId(Integer transferId) {
		this.transferId = transferId;
		return this;
	}
	public Double getQuantityDifference() {
		return quantityDifference;
	}
	public StockInfo setQuantityDifference(Double quantityDifference) {
		this.quantityDifference = quantityDifference;
		return this;
	}
	public String getProductName() {
		return productName;
	}
	public StockInfo setProductName(String productName) {
		this.productName = productName;
		return this;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public StockInfo setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
		return this;
	}
	
	
	
}
