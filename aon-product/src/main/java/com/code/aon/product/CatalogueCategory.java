package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a CatalogueCategory.
 * 
 * @author Consulting & Development. Gorka Irazu - 18/07/2008
 */
@Entity
@Table(name="catalogue_category")
public class CatalogueCategory implements ITransferObject {

	/** The Id. */
	private Integer Id;

	/** The catalogue. */
	private Catalogue catalogue;

	/** The product category. */
	private ProductCategory category;

	/** The quantity. */
	private double quantity;
	
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
	 * Gets the product category.
	 * 
	 * @return the product category
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="category", nullable=false)
	public ProductCategory getCategory() {
		return category;
	}

	/**
	 * Sets the product category.
	 * 
	 * @param category the product category
	 */
	public void setCategory(ProductCategory category) {
		this.category = category;
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

}
