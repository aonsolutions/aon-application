package com.esferalia.aon.occam.api.model.warehouse;

import java.util.List;

import com.esferalia.aon.occam.api.model.product.ItemComposition;

public class PackagingDeliveryContent {
	
	Integer source;
	List<ItemComposition> composition;
	
	public PackagingDeliveryContent() {
	
	}
	
	public Integer getSource() {
		return source;
	}
	
	public PackagingDeliveryContent setSource(Integer source) {
		this.source = source;
		return this;
	}
	
	public List<ItemComposition> getComposition() {
		return composition;
	}
	
	public PackagingDeliveryContent setComposition(List<ItemComposition> composition) {
		this.composition = composition;
		return this;
	}
	
}
