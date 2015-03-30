package com.esferalia.aon.gwt.template.server;

import com.code.aon.config.Series;
import com.esferalia.aon.gwt.template.shared.Warehouse;

public class TransferInfo {

	Series series;
	Warehouse sourceWarehouse;
	Warehouse targetWarehouse;
	String comments;
	
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

}
