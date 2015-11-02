package com.esferalia.aon.gwt.template.shared.marketplace;

public class Order {
	
	Integer id;
	String orderId;
	String serie;
	Integer number;
	AmazonDelivery amazonDelivery;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public AmazonDelivery getAmazonDelivery() {
		return amazonDelivery;
	}
	public void setAmazonDelivery(AmazonDelivery amazonDelivery) {
		this.amazonDelivery = amazonDelivery;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getSerie() {
		return serie;
	}
	public void setSerie(String serie) {
		this.serie = serie;
	}
	
	
}
