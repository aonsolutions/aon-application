package com.esferalia.aon.gwt.template.server;

import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class TransferInfo {

	Series series;
	Warehouse sourceWarehouse;
	Warehouse targetWarehouse;
	String comments;
	Integer number;
	
	public Series getSeries() {
		return series;
	}
	public void setSeries(Series series) {
		this.series = series;
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
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}

}
