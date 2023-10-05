package com.esferalia.aon.occam.api.model.warehouse;

public class PackagingDelivery {
	
	Integer delivery;
	PackagingDeliveryContainer container;
	PackagingDeliveryContent content;
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

	public PackagingDeliveryContent getContent() {
		return content;
	}

	public PackagingDelivery setContent(PackagingDeliveryContent content) {
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
