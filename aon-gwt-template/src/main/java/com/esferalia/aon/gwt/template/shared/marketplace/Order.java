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
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public String getDateStr(){
		return dateStr;
	}
	
	public void setDateStr(String dateStr){
		this.dateStr = dateStr;
	}
	
	public String getOrder(){
		return getSerie() + "/" + getNumber(); 
	}
	
}
