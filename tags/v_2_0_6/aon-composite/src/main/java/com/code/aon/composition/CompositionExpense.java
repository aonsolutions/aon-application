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

/**
 * Transfer Object that represents the expenses of a product composition.
 * 
 * @author Consulting & Development. Gorka Irazu - 21-jun-2006
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="composition_expense")
public class CompositionExpense implements ITransferObject, ILookupObject {

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Composition header.
     */
    private Composition composition;

    /**
     * Expense description.
     */
    private String description;

    /**
     * Expense quantity.
     */
    private double quantity;

    /**
     * Expense price.
     */
    private double price;

    /**
     * Constructor.
     * 
     */
    public CompositionExpense() {
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
     * Returns the expense description.
     * 
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Assigns the expense description.
     * 
     * @param description
     *          Expense description.
     */
    public void setDescription(String description) {
        this.description = description ;
    }

    /**
     * Returns the expense quantity.
     * 
     * @return double
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Assigns the expense quantity.
     * 
     * @param quantity
     *          Expense quantity.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the expense price.
     * 
     * @return double
     */
    public double getPrice() {
        return price;
    }

    /**
     * Assigns the expense price.
     * 
     * @param price
     *          Expense price.
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