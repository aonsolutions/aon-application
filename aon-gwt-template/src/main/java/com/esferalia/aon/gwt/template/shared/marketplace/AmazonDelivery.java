package com.esferalia.aon.gwt.template.shared.marketplace;

import java.util.Date;

import com.esferalia.aon.gwt.template.client.marketplace.IMarketplace;
import com.esferalia.aon.gwt.template.client.marketplace.IMarketplaceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AmazonDelivery implements IsSerializable{

	String orderId;
	String orderItemId;
	Integer quantity;
	Date shipDate;
	String shipDateStr;
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

	public String getShipDateStr() {
		return shipDateStr;
	}

	public void setShipDateStr(String shipDateStr) {
		this.shipDateStr = shipDateStr;
	}
	
	public void setShipDateStr(Date date) {
		IMarketplaceAsync impl = GWT.create(IMarketplace.class);
		impl.getDateStr(date, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				shipDateStr = result;				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});

	}
	
}
