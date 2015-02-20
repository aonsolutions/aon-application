package com.esferalia.aon.gwt.template.server;

import com.code.aon.config.Series;
import com.google.gwt.user.client.rpc.IsSerializable;

public class StockInfo implements IsSerializable{
	
	String product;
	Series series;
	Warehouse sourceWarehouse;
	Warehouse targetWarehouse;
	String detail;
	String detail2;
	String detail3;
	String comments;
	Double quantity;
	
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
	public Series getSeries() {
		return series;
	}
	public void setSeries(Series series) {
		this.series = series;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	

}
