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
	
	Double sales;
	Double salesValue;
	
	Double purchases;
	Double purchasesValue;
	
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
	
	
	
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Double getQuantity() {
		return quantity;
	}
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getSales() {
		return sales;
	}
	public void setSales(Double sales) {
		this.sales = sales;
	}
	public Double getPurchases() {
		return purchases;
	}
	public void setPurchases(Double purchases) {
		this.purchases = purchases;
	}
	public Double getTransfersPlus() {
		return transfersPlus;
	}
	public void setTransfersPlus(Double transfersPlus) {
		this.transfersPlus = transfersPlus;
	}
	public Double getTransfersMinus() {
		return transfersMinus;
	}
	public void setTransfersMinus(Double transfersMinus) {
		this.transfersMinus = transfersMinus;
	}
	public Double getConsumption() {
		return consumption;
	}
	public void setConsumption(Double consumption) {
		this.consumption = consumption;
	}
	public Double getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(Double itemPrice) {
		this.itemPrice = itemPrice;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getDetail2() {
		return detail2;
	}
	public void setDetail2(String detail2) {
		this.detail2 = detail2;
	}
	public String getDetail3() {
		return detail3;
	}
	public void setDetail3(String detail3) {
		this.detail3 = detail3;
	}
	public Double getInitialQuantity() {
		return initialQuantity;
	}
	public void setInitialQuantity(Double initialQuantity) {
		this.initialQuantity = initialQuantity;
	}
	public Double getFinalQuantity() {
		return finalQuantity;
	}
	public void setFinalQuantity(Double finalQuantity) {
		this.finalQuantity = finalQuantity;
	}
	public Double getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(Double initialValue) {
		this.initialValue = initialValue;
	}
	public Double getFinalValue() {
		return finalValue;
	}
	public void setFinalValue(Double finalValue) {
		this.finalValue = finalValue;
	}
	public Double getSalesValue() {
		return salesValue;
	}
	public void setSalesValue(Double salesValue) {
		this.salesValue = salesValue;
	}
	public Double getPurchasesValue() {
		return purchasesValue;
	}
	public void setPurchasesValue(Double purchasesValue) {
		this.purchasesValue = purchasesValue;
	}
	public Double getTransfersPlusValue() {
		return transfersPlusValue;
	}
	public void setTransfersPlusValue(Double transfersPlusValue) {
		this.transfersPlusValue = transfersPlusValue;
	}
	public Double getTransfersMinusValue() {
		return transfersMinusValue;
	}
	public void setTransfersMinusValue(Double transfersMinusValue) {
		this.transfersMinusValue = transfersMinusValue;
	}
	public Integer getInitialId() {
		return initialId;
	}
	public void setInitialId(Integer initialId) {
		this.initialId = initialId;
	}
	public Date getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}
	public Integer getFinalId() {
		return finalId;
	}
	public void setFinalId(Integer finalId) {
		this.finalId = finalId;
	}
	public Date getFinalDate() {
		return finalDate;
	}
	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}
	public Integer getWarehouseId() {
		return warehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	public String getWarehouseName() {
		return warehouseName;
	}
	public void setWarehouseName(String warehouseName) {
		this.warehouseName = warehouseName;
	}
	public String getInitialInventoryName() {
		return initialInventoryName;
	}
	public void setInitialInventoryName(String initialInventoryName) {
		this.initialInventoryName = initialInventoryName;
	}
	public String getFinalInventoryName() {
		return finalInventoryName;
	}
	public void setFinalInventoryName(String finalInventoryName) {
		this.finalInventoryName = finalInventoryName;
	}
	
	
}
