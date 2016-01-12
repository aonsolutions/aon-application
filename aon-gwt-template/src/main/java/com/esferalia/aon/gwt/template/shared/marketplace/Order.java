package com.esferalia.aon.gwt.template.shared.marketplace;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Order implements IsSerializable{
	
	Integer id;
	String orderId;
	String serie;
	Integer number;
	Date date;
	String dateStr;
	String customerName;
	Double price;
	AmazonDelivery amazonDelivery;
	String sellerName;
	
	public Integer getId() {
		return id;
	}
	public Order setId(Integer id) {
		this.id = id;
		return this;
	}
	public AmazonDelivery getAmazonDelivery() {
		return amazonDelivery;
	}
	public Order setAmazonDelivery(AmazonDelivery amazonDelivery) {
		this.amazonDelivery = amazonDelivery;
		return this;
	}
	public String getOrderId() {
		return orderId;
	}
	public Order setOrderId(String orderId) {
		this.orderId = orderId;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public Order setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public String getSerie() {
		return serie;
	}
	public Order setSerie(String serie) {
		this.serie = serie;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public Order setDate(Date date) {
		this.date = date;
		return this;
	}
	public String getCustomerName() {
		return customerName;
	}
	public Order setCustomerName(String customerName) {
		this.customerName = customerName;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public Order setPrice(Double price) {
		this.price = price;
		return this;
	}
	
	public String getDateStr(){
		return dateStr;
	}
	
	public Order setDateStr(String dateStr){
		this.dateStr = dateStr;
		return this;
	}
	
	public String getOrder(){
		return getSerie() + "/" + getNumber(); 
	}
	public String getSellerName() {
		return sellerName;
	}
	public Order setSellerName(String sellerName) {
		this.sellerName = sellerName;
		return this;
	}
}
