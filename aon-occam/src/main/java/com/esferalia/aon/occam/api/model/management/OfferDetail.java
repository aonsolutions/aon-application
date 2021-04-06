package com.esferalia.aon.occam.api.model.management;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.type.OfferDetailStatus;

public class OfferDetail implements Serializable {

	private static final long serialVersionUID = 7597157186868662372L;
	
	private Integer id;
	private Integer domain;
	private Offer offer;
	private OldItem item;
	private Short line;
	private String description;
	private Double quantity;
	private Double price;
	private String discountExpression;
	private OfferDetailStatus status;
	
	public Integer getId() {
		return id;
	}
	public OfferDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public OfferDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public Offer getOffer() {
		return offer;
	}
	public OfferDetail setOffer(Offer offer) {
		this.offer = offer;
		return this;
	}
	public OldItem getItem() {
		return item;
	}
	public OfferDetail setItem(OldItem item) {
		this.item = item;
		return this;
	}
	public short getLine() {
		return line;
	}
	public OfferDetail setLine(Short line) {
		this.line = line;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public OfferDetail setDescription(String description) {
		this.description = description;
		return this;
	}
	public double getQuantity() {
		return quantity;
	}
	public OfferDetail setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}
	public double getPrice() {
		return price;
	}
	public OfferDetail setPrice(Double price) {
		this.price = price;
		return this;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public OfferDetail setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
	public OfferDetailStatus getStatus() {
		return status;
	}
	public OfferDetail setStatus(OfferDetailStatus status) {
		this.status = status;
		return this;
	}
	
	
}
