package com.code.aon.composition;

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
import com.code.aon.product.Item;

/**
 * Transfer Object that represents the lines of a product production.
 * 
 * @author Consulting & Development. Gorka Irazu - 23-oct-2006
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="production_detail")
public class ProductionDetail implements ITransferObject {

	private static final long serialVersionUID = -3731542135760165592L;

	/**
     * Unique key.
     */
    private Integer id;

    /**
     * Production header.
     */
    private Production production;

    /**
     * Item derivative from production.
     */
    private Item item;

    /**
     * Item derivative description.
     */
    private String description;

    /**
     * Initial quantity of item derivative in composition.
     */
    private double initialQuantity;

    /**
     * Quantity of item derivative.
     */
    private double quantity;

    /**
     * Item derivative price.
     */
    private double price;

    /**
     * Returns the unique key.
     * 
     * @return Integer
     */
    @Id
    @GeneratedValue
    @Column(nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Assigns the unique key.
     * 
     * @param id
     *          Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the production header.
     * 
     * @return Production
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="production",nullable=false)
    public Production getProduction() {
        return production;
    }

    /**
     * Assigns the production header.
     * 
     * @param production
     *          Production header.
     */
    public void setProduction(Production production) {
        this.production = production;
    }

    /**
     * Returns the item derivative from production.
     * 
     * @return Item
     *      cascade = "none"
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="item", nullable=false)
    public Item getItem() {
        return item;
    }

    /**
     * Assigns the item derivative from production.
     * 
     * @param item
     *          Item derivative from production.
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Returns the item derivative description.
     * 
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Assigns the item derivative description.
     * 
     * @param description
     *          Item derivative description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the initial quantity of item derivative in composition.
     * 
     * @return double
     */
    @Column(name="initial_quantity")
    public double getInitialQuantity() {
        return initialQuantity;
    }

    /**
     * Assigns the initial quantity of item derivative in composition.
     * 
     * @param initialQuantity
     *          Initial quantity of item derivative in composition.
     */
    public void setInitialQuantity(double initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    /**
     * Returns the quantity of item derivative.
     * 
     * @return double
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Assigns the quantity of item derivative.
     * 
     * @param quantity
     *          Quantity of item derivative.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the item derivative price.
     * 
     * @return double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Assigns the item derivative price.
     * 
     * @param price
     *          Item derivative price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof ProductionDetail) {
			ProductionDetail o = (ProductionDetail) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}