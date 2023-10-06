package com.esferalia.aon.occam.api.model.warehouse;

public class PackagingDeliveryContainer {
	
	Integer item;
	Integer product;
	
	public PackagingDeliveryContainer() {
	
	}
	
	public Integer getItem() {
		return item;
	}
	
	public PackagingDeliveryContainer setItem(Integer item) {
		this.item = item;
		return this;
	}
	
	public Integer getProduct() {
		return product;
	}
	
	public PackagingDeliveryContainer setProduct(Integer product) {
		this.product = product;
		return this;
	}
	
}
