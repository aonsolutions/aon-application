package com.esferalia.aon.occam.api.model.warehouse;

import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.registry.Supplier;

public class UdapaQuality {

	DataResponse dataResponse;
	Supplier supplier;
	String product;
	
	public UdapaQuality() {

	}

	public DataResponse getDataResponse() {
		return dataResponse;
	}

	public UdapaQuality setDataResponse(DataResponse dataResponse) {
		this.dataResponse = dataResponse;
		return this;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public UdapaQuality setSupplier(Supplier supplier) {
		this.supplier = supplier;
		return this;
	}

	public String getProduct() {
		return product;
	}

	public UdapaQuality setProduct(String product) {
		this.product = product;
		return this;
	}
	
}
