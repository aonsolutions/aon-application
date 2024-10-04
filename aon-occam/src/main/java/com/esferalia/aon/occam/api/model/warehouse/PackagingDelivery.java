package com.esferalia.aon.occam.api.model.warehouse;

import java.util.LinkedList;
import java.util.List;

public class PackagingDelivery {
	
	Integer delivery;
	PackagingDeliveryContainer container;
	List<PackagingDeliveryContent> content;
	Integer salesDetail;
	
	public PackagingDelivery() {

	}

	public Integer getDelivery() {
		return delivery;
	}

	public PackagingDelivery setDelivery(Integer delivery) {
		this.delivery = delivery;
		return this;
	}

	public PackagingDeliveryContainer getContainer() {
		return container;
	}

	public PackagingDelivery setContainer(PackagingDeliveryContainer container) {
		this.container = container;
		return this;
	}

	public List<PackagingDeliveryContent> getContent() {
		if(content == null) {
			content = new LinkedList<>();
		}
		return content;
	}

	public PackagingDelivery setContent(List<PackagingDeliveryContent> content) {
		this.content = content;
		return this;
	}
	
	public Integer getSalesDetail() {
		return salesDetail;
	}
	
	public PackagingDelivery setSalesDetail(Integer salesDetail) {
		this.salesDetail = salesDetail;
		return this;
	}

}
