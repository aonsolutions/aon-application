package com.esferalia.aon.gwt.template.shared.marketplace;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AmazonDelivery implements IsSerializable{

	String orderId;
	String orderItemId;
	Integer quantity;
	Date shipDate;
	CarrierCode carrierCode;
	String carrierName;
	String trackingNumber;
	String shipMethod;
	
	public AmazonDelivery() {
	
	}
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderItemId() {
		return orderItemId;
	}
	public void setOrderItemId(String orderItemId) {
		this.orderItemId = orderItemId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public CarrierCode getCarrierCode() {
		return carrierCode;
	}
	public void setCarrierCode(CarrierCode carrierCode) {
		this.carrierCode = carrierCode;
	}
	public String getCarrierName() {
		return carrierName;
	}
	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}
	public Date getShipDate() {
		return shipDate;
	}
	public void setShipDate(Date shipDate) {
		this.shipDate = shipDate;
	}
	public String getShipMethod() {
		return shipMethod;
	}
	public void setShipMethod(String shipMethod) {
		this.shipMethod = shipMethod;
	}
	public String getTrackingNumber() {
		return trackingNumber;
	}
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
}
