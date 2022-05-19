package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;

public class SalesDetail implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2246636570307088465L;
	
	private Integer id;
	private int domain;
	private Sales sales;
	private Item item;
	private short line;
	private String description;
	private double quantity;
	private double price;
	private String discountExpression;
	private double taxes;
	private SalesDetailStatus status;
	private Integer offerDetail;
	private double delivered;
	private Date deliveryDate;
	private Carrier carrier;
	private Integer carrierPacking;
	
	public Integer getId() {
		return id;
	}
	
	public SalesDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public int getDomain() {
		return domain;
	}
	
	public SalesDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	public Sales getSales() {
		return sales;
	}
	
	public SalesDetail setSales(Sales sales) {
		this.sales = sales;
		return this;
	}
	
	public Item getItem() {
		if(item == null) 
			item = new Item();
		return item;
	}
	
	public SalesDetail setItem(Item item) {
		this.item = item;
		return this;
	}
	
	public short getLine() {
		return line;
	}
	
	public SalesDetail setLine(short line) {
		this.line = line;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	
	public SalesDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public double getQuantity() {
		return quantity;
	}
	
	public SalesDetail setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	
	public double getPrice() {
		return price;
	}
	
	public SalesDetail setPrice(double price) {
		this.price = price;
		return this;
	}
	
	public String getDiscountExpression() {
		return discountExpression;
	}
	
	public SalesDetail setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	
	public double getTaxes() {
		return taxes;
	}
	
	public SalesDetail setTaxes(double taxes) {
		this.taxes = taxes;
		return this;
	}
	
	public SalesDetailStatus getStatus() {
		return status;
	}
	
	public SalesDetail setStatus(SalesDetailStatus status) {
		this.status = status;
		return this;
	}
	
	public Integer getOfferDetail() {
		return offerDetail;
	}
	
	public SalesDetail setOfferDetail(Integer offerDetail) {
		this.offerDetail = offerDetail;
		return this;
	}
	
	public double getDelivered() {
		return delivered;
	}
	
	public SalesDetail setDelivered(double delivered) {
		this.delivered = delivered;
		return this;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public SalesDetail setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
		return this;
	}

	public Carrier getCarrier() {
		if(carrier == null) {
			carrier = new Carrier();
		}
		return carrier;
	}

	public SalesDetail setCarrier(Carrier carrier) {
		this.carrier = carrier;
		return this;
	}

	public Integer getCarrierPacking() {
		return carrierPacking;
	}

	public SalesDetail setCarrierPacking(Integer carrierPacking) {
		this.carrierPacking = carrierPacking;
		return this;
	}
		
}
