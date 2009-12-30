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
 * Transfer Object that represents the expenses of a product production.
 * 
 * @author Consulting & Development. Gorka Irazu - 21-jun-2006
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="production_expense")
public class ProductionExpense implements ITransferObject, ILookupObject {

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Production header.
     */
    private Production production;

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
    public ProductionExpense() {
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
     * Assigns the composition header.
     * 
     * @param production
     *          Production header.
     */
    public void setProduction(Production production) {
        this.production = production;
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