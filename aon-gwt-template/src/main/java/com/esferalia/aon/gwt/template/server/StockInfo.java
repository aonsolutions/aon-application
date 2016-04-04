package com.esferalia.aon.gwt.template.server;

import com.esferalia.aon.occam.api.model.product.Item;
import com.google.gwt.user.client.rpc.IsSerializable;

public class StockInfo implements IsSerializable{
	
	String product;
	Integer productId;
	Double quantity;
	Integer row;
	Integer domainId;
	Integer transferId;
	Double quantityDifference;
	String productName;
	String workplaceStr;
	String departmentStr;
	
	Item item;
	
	public Item getItem() {
		return item;
	}
	public StockInfo setItem(Item item) {
		this.item = item;
		return this;
	}
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

}
