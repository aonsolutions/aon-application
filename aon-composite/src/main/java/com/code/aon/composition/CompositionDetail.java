package com.code.aon.composition;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.product.Item;

/**
 * Transfer Object that represents the lines of a product composition.
 * 
 * @author Consulting & Development. Gorka Irazu - 29-may-2006
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="composition_detail")
public class CompositionDetail implements ITransferObject, ILookupObject {

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Composition header.
     */
    private Composition composition;

    /**
     * Item derivative from composition.
     */
    private Item item;

    /**
     * Item derivative description.
     */
    private String description;

    /**
     * Quantity of item derivative.
     */
    private double quantity;

    /**
     * Item derivative price.
     */
    private double price;

    /**
     * Constructor.
     * 
     */
    public CompositionDetail() {
    }

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
     * Returns the composition header.
     * 
     * @return Composition
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="composition",nullable=false)
    public Composition getComposition() {
        return composition;
    }

    /**
     * Assigns the composition header.
     * 
     * @param composition
     *          Composition header.
     */
    public void setComposition(Composition composition) {
        this.composition = composition;
    }

    /**
     * Returns the item derivative from composition.
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
     * Assigns the item derivative from composition.
     * 
     * @param item
     *          Item derivative from composition.
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

    /**
     * Returns a Map containing some attributes to use as lookups.
     *
     * @return Map<String,Object>
     */
	@Transient
    public Map<String,Object> getLookups() {
        Map<String,Object> map = new HashMap<String,Object>();
        return map;
    }

}