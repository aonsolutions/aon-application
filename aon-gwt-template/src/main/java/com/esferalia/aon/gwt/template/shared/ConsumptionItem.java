package com.esferalia.aon.gwt.template.shared;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ConsumptionItem implements IsSerializable{
	
	Integer itemId;
	String productCode;
	String productName;
	
	Double quantity;
	String discount;
	Double price;
	
	String initialInventoryName;
	Integer initialId;
	Date initialDate;
	Double initialQuantity;
	Double initialValue;
	
	String finalInventoryName;
	Integer finalId;
	Date finalDate;
	Double finalQuantity;
	Double finalValue;
	
	Integer warehouseId;
	String warehouseName;
	
	Double salesAlb;
	Double salesValueAlb;
	Double salesFac;
	Double salesValueFac;
	
	Double purchasesAlb;
	Double purchasesValueAlb;
	Double purchasesFac;
	Double purchasesValueFac;
	
	Double valuePAlb;
	Double valuePFac;
	Double valueSAlb;
	Double valueSFac;
	
	Double transfersPlus;
	Double transfersPlusValue;
	
	Double transfersMinus;
	Double transfersMinusValue;
	
	Double consumption;
	Double itemPrice;
	Double totalPrice;
	
	String detail;
	String detail2;
	String detail3;
	
	String hotel;
		
	public Double getValuePAlb() {
		return valuePAlb;
	}
	public ConsumptionItem setValuePAlb(Double value) {
		this.valuePAlb = value;
		return this;
	}
	public Double getValuePFac() {
		return valuePFac;
	}
	public ConsumptionItem setValuePFac(Double value) {
		this.valuePFac = value;
		return this;
	}
	public Double getValueSAlb() {
		return valueSAlb;
	}
	public ConsumptionItem setValueSAlb(Double value) {
		this.valueSAlb = value;
		return this;
	}
	public Double getValueSFac() {
		return valueSFac;
	}
	public ConsumptionItem setValueSFac(Double value) {
		this.valueSFac = value;
		return this;
	}
	public Integer getItemId() {
		return itemId;
	}
	public ConsumptionItem setItemId(Integer itemId) {
		this.itemId = itemId;
		return this;
	}
	public String getProductCode() {
		return productCode;
	}
	public ConsumptionItem setProductCode(String productCode) {
		this.productCode = productCode;
		return this;
	}
	public String getProductName() {
		return productName;
	}
	public ConsumptionItem setProductName(String productName) {
		this.productName = productName;
		return this;
	}
	public Double getQuantity() {
		return quantity;
	}
	public ConsumptionItem setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public String getDiscount() {
		return discount;
	}
	public ConsumptionItem setDiscount(String discount) {
		this.discount = discount;
		return this;
	}
	public Double getPrice() {
		return price;
	}
	public ConsumptionItem setPrice(Double price) {
		this.price = price;
		return this;
	}
	public Double getSalesAlb() {
		return salesAlb;
	}
	public ConsumptionItem setSalesAlb(Double sales) {
		this.salesAlb = sales;
		return this;
	}
	public Double getSalesFac() {
		return salesFac;
	}
	public ConsumptionItem setSalesFac(Double sales) {
		this.salesFac = sales;
		return this;
	}
	public Double getPurchasesAlb() {
		return purchasesAlb;
	}
	public ConsumptionItem setPurchasesAlb(Double purchases) {
		this.purchasesAlb = purchases;
		return this;
	}
	public Double getPurchasesFac() {
		return purchasesFac;
	}
	public ConsumptionItem setPurchasesFac(Double purchases) {
		this.purchasesFac = purchases;
		return this;
	}
	public Double getTransfersPlus() {
		return transfersPlus;
	}
	public ConsumptionItem setTransfersPlus(Double transfersPlus) {
		this.transfersPlus = transfersPlus;
		return this;
	}
	public Double getTransfersMinus() {
		return transfersMinus;
	}
	public ConsumptionItem setTransfersMinus(Double transfersMinus) {
		this.transfersMinus = transfersMinus;
		return this;
	}
	public Double getConsumption() {
		return consumption;
	}
	public ConsumptionItem setConsumption(Double consumption) {
		this.consumption = consumption;
		return this;
	}
	public Double getItemPrice() {
		return itemPrice;
	}
	public ConsumptionItem setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
		return this;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public ConsumptionItem setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
		return this;
	}
	public String getDetail() {
		return detail;
	}
	public ConsumptionItem setDetail(String detail) {
		this.detail = detail;
		return this;
	}
	public String getDetail2() {
		return detail2;
	}
	public ConsumptionItem setDetail2(String detail2) {
		this.detail2 = detail2;
		return this;
	}
	public String getDetail3() {
		return detail3;
	}
	public ConsumptionItem setDetail3(String detail3) {
		this.detail3 = detail3;
		return this;
	}
	public Double getInitialQuantity() {
		return initialQuantity;
	}
	public ConsumptionItem setInitialQuantity(Double initialQuantity) {
		this.initialQuantity = initialQuantity;
		return this;
	}
	public Double getFinalQuantity() {
		return finalQuantity;
	}
	public ConsumptionItem setFinalQuantity(Double finalQuantity) {
		this.finalQuantity = finalQuantity;
		return this;
	}
	public Double getInitialValue() {
		return initialValue;
	}
	public ConsumptionItem setInitialValue(Double initialValue) {
		this.initialValue = initialValue;
		return this;
	}
	public Double getFinalValue() {
		return finalValue;
	}
	public ConsumptionItem setFinalValue(Double finalValue) {
		this.finalValue = finalValue;
		return this;
	}
	public Double getSalesValueAlb() {
		return salesValueAlb;
	}
	public ConsumptionItem setSalesValueAlb(Double salesValue) {
		this.salesValueAlb = salesValue;
		return this;
	}
	public Double getSalesValueFac() {
		return salesValueFac;
	}
	public ConsumptionItem setSalesValueFac(Double salesValue) {
		this.salesValueFac = salesValue;
		return this;
	}
	public Double getPurchasesValueAlb() {
		return purchasesValueAlb;
	}
	public ConsumptionItem setPurchasesValueAlb(Double purchasesValue) {
		this.purchasesValueAlb = purchasesValue;
		return this;
	}
	public Double getPurchasesValueFac() {
		return purchasesValueFac;
	}
	public ConsumptionItem setPurchasesValueFac(Double purchasesValue) {
		this.purchasesValueFac = purchasesValue;
		return this;
	}
	public Double getTransfersPlusValue() {
		return transfersPlusValue;
	}
	public ConsumptionItem setTransfersPlusValue(Double transfersPlusValue) {
		this.transfersPlusValue = transfersPlusValue;
		return this;
	}
	public Double getTransfersMinusValue() {
		return transfersMinusValue;
	}
	public ConsumptionItem setTransfersMinusValue(Double transfersMinusValue) {
		this.transfersMinusValue = transfersMinusValue;
		return this;
	}
	public Integer getInitialId() {
		return initialId;
	}
	public ConsumptionItem setInitialId(Integer initialId) {
		this.initialId = initialId;
		return this;
	}
	public Date getInitialDate() {
		return initialDate;
	}
	public ConsumptionItem setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
		return this;
	}
	public Integer getFinalId() {
		return finalId;
	}
	public ConsumptionItem setFinalId(Integer finalId) {
		this.finalId = finalId;
		return this;
	}
	public Date getFinalDate() {
		return finalDate;
	}
	public ConsumptionItem setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
		return this;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public ConsumptionItem setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
		return this;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public ConsumptionItem setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
		return this;
	}
	public String getInitialInventoryName() {
		return initialInventoryName;
	}
	public ConsumptionItem setInitialInventoryName(String initialInventoryName) {
		this.initialInventoryName = initialInventoryName;
		return this;
	}
	public String getFinalInventoryName() {
		return finalInventoryName;
	}
	public ConsumptionItem setFinalInventoryName(String finalInventoryName) {
		this.finalInventoryName = finalInventoryName;
		return this;
	}
	public String getHotel() {
		return hotel;
	}
	public ConsumptionItem setHotel(String hotel) {
		this.hotel = hotel;
		return this;
	}
	
	
	public Double getConsumValue(){
		Double consumValue = (getInitialValue() * getInitialQuantity()) 
				+  	(getPurchasesAlb() * getPurchasesValueAlb())
				+  	(getPurchasesFac() * getPurchasesValueFac())
				+	((getTransfersPlus() * getPrice()) - (getTransfersMinus() * getPrice()))
				-	(getSalesAlb() * getSalesValueAlb())
				-	(getSalesFac() * getSalesValueFac())
				-	(getFinalQuantity() * getFinalValue());
		
		if(consumValue != null) return consumValue;
		else return 0.0;	
	}
}
