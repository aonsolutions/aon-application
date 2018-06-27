package com.esferalia.aon.occam.api.model.warehouse;

import com.esferalia.aon.occam.api.model.DataResponse;

public class PaturpatQuality {
	DataResponse dataResponse;
	String product;
	public PaturpatQuality() {
	
	}
	
	public DataResponse getDataResponse() {
		return dataResponse;
	}
	
	public PaturpatQuality setDataResponse(DataResponse dataResponse) {
		this.dataResponse = dataResponse;
		return this;
	}
	
	public String getProduct() {
		return product;
	}
	
	public PaturpatQuality setProduct(String product) {
		this.product = product;
		return this;
	}
}
