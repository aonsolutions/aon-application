package com.esferalia.aon.gwt.template.server;

import com.code.aon.config.Series;
import com.esferalia.aon.gwt.template.shared.Warehouse;
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
	
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getDetail2() {
		return detail2;
	}
	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}
	public String getDetail3() {
		return detail3;
	}
	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public Integer getTransferId() {
		return transferId;
	}
	public void setTransferId(Integer transferId) {
		this.transferId = transferId;
	}
	public Double getQuantityDifference() {
		return quantityDifference;
	}
	public void setQuantityDifference(Double quantityDifference) {
		this.quantityDifference = quantityDifference;
	}
	
	
}
