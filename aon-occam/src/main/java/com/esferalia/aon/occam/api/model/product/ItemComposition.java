package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public class ItemComposition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4896779297825068549L;
	private Integer id;
	private int domain;
	private int itemId;
	private int compositionItemId;
	private Item composition;
	private int sequence;
	private String description;
	private double quantity;
	private String discountExpression;
	public Integer getId() {
		return id;
	}
	public ItemComposition setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public ItemComposition setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public int getItemId() {
		return itemId;
	}
	public ItemComposition setItemId(int itemId) {
		this.itemId = itemId;
		return this;
	}
	public int getCompositionItemId() {
		return compositionItemId;
	}
	public ItemComposition setCompositionItemId(int compositionItemId) {
		this.compositionItemId = compositionItemId;
		return this;
	}
	
	public Item getComposition() {
		return composition;
	}
	
	public ItemComposition setComposition(Item composition) {
		this.composition = composition;
		return this;
	}
	
	public Integer getSequence() {
		return sequence;
	}
	public ItemComposition setSequence(int sequence) {
		this.sequence = sequence;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public ItemComposition setDescription(String description) {
		this.description = description;
		return this;
	}
	public double getQuantity() {
		return quantity;
	}
	public ItemComposition setQuantity(double quantity) {
		this.quantity = quantity;
		return this;
	}
	public String getDiscountExpression() {
		return discountExpression;
	}
	public ItemComposition setDiscountExpression(String discountExpression) {
		this.discountExpression = discountExpression;
		return this;
	}
		
}
