package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a CatalogueItem.
 * 
 * @author Consulting & Development. Gorka Irazu - 18/07/2008
 */
@Entity
@Table(name="catalogue_item")
public class CatalogueItem implements ITransferObject {

	private static final long serialVersionUID = 9026585458569586807L;

	/** The Id. */
	private Integer Id;

	/** The catalogue. */
	private Catalogue catalogue;

	/** The item. */
	private Item item;

	/** The quantity. */
	private double quantity;
	
	/** The price. */
	private double price;
	
	/** The discount. */
	private double discount;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return Id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		Id = id;
	}

	/**
	 * Gets the catalogue.
	 * 
	 * @return the catalogue
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="catalogue", nullable=false)
	public Catalogue getCatalogue() {
		return catalogue;
	}

	/**
	 * Sets the catalogue.
	 * 
	 * @param catalogue the catalogue
	 */
	public void setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="item", nullable=false)
	public Item getItem() {
		return item;
	}

	/**
	 * Sets the item.
	 * 
	 * @param item the item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Gets the quantity.
	 * 
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * Sets the quantity.
	 * 
	 * @param quantity the quantity
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	/**
	 * Gets the price.
	 * 
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 * 
	 * @param price the price
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Gets the discount.
	 * 
	 * @return the discount
	 */
	public double getDiscount() {
		return discount;
	}

	/**
	 * Sets the discount.
	 * 
	 * @param discount the discount
	 */
	public void setDiscount(double discount) {
		this.discount = discount;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof CatalogueItem) {
			CatalogueItem catalogueItem = (CatalogueItem) obj;
			if (ObjectUtils.equals(getId(), catalogueItem.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
