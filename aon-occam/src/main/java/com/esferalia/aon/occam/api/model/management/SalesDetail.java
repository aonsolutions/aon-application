package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;

public class SalesDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2246636570307088465L;
	
	private Integer id;
	private int domain;
	private int sales;
	private int item;
	private short line;
	private String description;
	private double quantity;
	private double price;
	private String discountExpression;
	private double taxes;
	private SalesDetailStatus status;
	private int offerDetail;
	private double delivered;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public int getSales() {
		return sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}
	public int getItem() {
		return item;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public short getLine() {
		return line;
	}
	public void setLine(short line) {
		this.line = line;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public void setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
	}
	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}
	public SalesDetailStatus getStatus() {
		return status;
	}
	public void setStatus(SalesDetailStatus status) {
		this.status = status;
	}
	public int getOfferDetail() {
		return offerDetail;
	}
	public void setOfferDetail(int offerDetail) {
		this.offerDetail = offerDetail;
	}
	public double getDelivered() {
		return delivered;
	}
	public void setDelivered(double delivered) {
		this.delivered = delivered;
	}
		
}
