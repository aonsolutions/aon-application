package com.code.aon.marketplace.tickets;

import java.util.Date;

/**
 * Class that represents a Ticket.
 */
public class Ticket {

	/** The item. */
	private String item;

	/** The sanatary id. */
	private String sanataryId;
	
	/** The ingredients. */
	private String ingredients;

	/** The quantity. */
	private int quantity = 1;

	/** The net quantity. */
	private String netQuantity;
	
	/** The expiry date. */
	private Date expiry;

	/** The conditions. */
	private String conditions;
	
	/**
	 * Gets the ingredients.
	 * 
	 * @return the ingredients
	 */
	public String getIngredients() {
		return ingredients;
	}

	/**
	 * Sets the ingredients.
	 * 
	 * @param ingredients the ingredients
	 */
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	public String getItem() {
		return item;
	}

	/**
	 * Sets the item.
	 * 
	 * @param item the item
	 */
	public void setItem(String item) {
		this.item = item;
	}

	/**
	 * Gets the sanatary id.
	 * 
	 * @return the sanatary id
	 */
	public String getSanataryId() {
		return sanataryId;
	}

	/**
	 * Sets the sanatary id.
	 * 
	 * @param sanataryId the sanatary id
	 */
	public void setSanataryId(String sanataryId) {
		this.sanataryId = sanataryId;
	}

	/**
	 * Gets the quantity.
	 * 
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity.
	 * 
	 * @param quantity the quantity
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the conditions.
	 * 
	 * @return the conditions
	 */
	public String getConditions() {
		return conditions;
	}

	/**
	 * Sets the conditions.
	 * 
	 * @param conditions the conditions
	 */
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	/**
	 * Gets the expiry date.
	 * 
	 * @return the expiry date
	 */
	public Date getExpiry() {
		return expiry;
	}

	/**
	 * Sets the expiry date.
	 * 
	 * @param expiry the expiry date
	 */
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	/**
	 * Gets the net quantity.
	 * 
	 * @return the net quantity
	 */
	public String getNetQuantity() {
		return netQuantity;
	}

	/**
	 * Sets the net quantity.
	 * 
	 * @param netQuantity the net quantity
	 */
	public void setNetQuantity(String netQuantity) {
		this.netQuantity = netQuantity;
	}
}