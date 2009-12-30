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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents a CatalogueItem.
 * 
 * @author Consulting & Development. Gorka Irazu - 18/07/2008
 */
@Entity
@Table(name="catalogue_item")
public class CatalogueItem implements ITransferObject {

	private static final long serialVersionUID = 9026585458569586807L;

	/** The id. */
	private Integer id;

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
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the catalogue.
	 * 
	 * @return the catalogue
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="catalogue", nullable=false)
	@ForeignKey(name = "FK_CATALOGUE_ITEM_CATALOGUE")
	@Index(name = "IDX_CATALOGUE_ITEM_CATALOGUE")		
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
	@ForeignKey(name = "FK_CATALOGUE_ITEM_ITEM")
	@Index(name = "IDX_CATALOGUE_ITEM_ITEM")	
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
	@Column(nullable = true)
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
	@Column(nullable = true)
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
	@Column(nullable = true, precision = 6, scale = 2)
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
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CatalogueItem o = (CatalogueItem) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.catalogue, o.catalogue)				
				.append(this.discount, o.discount)				
				.append(this.item, o.item)				
				.append(this.price, o.price)								
				.append(this.quantity, o.quantity)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(catalogue)
			.append(discount)
			.append(id)			
			.append(item)						
			.append(price)						
			.append(quantity)						
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
