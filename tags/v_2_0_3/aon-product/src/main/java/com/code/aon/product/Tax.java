package com.code.aon.product;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.TaxType;

/**
 * Transfer Object that represents a tax.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="tax")
public class Tax implements ITransferObject, ILookupObject {

    /**
     * Unique key.
     */
    private Integer id;

    /**
     * The tax name.
     */
    private String name;

    /**
     * The tax type.
     */
    private TaxType type;

    /**
     * This tax percentage to be applied.
     */
    private double percentage;

    /**
     * Surcharge to be applied.
     */
    private double surcharge;

    /**
     * Last date for this tax to be applied.
     */
    private Date startDate;

    /**
     * Void constructor.
     * 
     */
    public Tax() {
    }

    /**
     * Constructor for this id.
     * 
     * @param pk
     *            Unique key.
     */
    public Tax(Integer pk) {
        this.id = pk;
    }

    /**
     * Returns the unique key.
     * 
     * @return returns the unique key.
     */
    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }

    /**
     * Assigns the unique key.
     * 
     * @param id
     *            unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Return this tax name.
     * 
     * @return tax name.
     */
    @Column(length=30, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assigns the tax name.
     * 
     * @param name
     *            Tax name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the percentage to be applied in this tax.
     * 
     * @return percentage.
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Assigns the percentage to be applied in this tax.
     * 
     * @param percentage
     *            the percentage to be applied in this tax.
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * Returns the top date for this tax to be applied.
     * 
     * @return  the top date for this tax to be applied.
     */
    @Column(name="start_date")
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Assigns the top date for this tax to be applied.
     * 
     * @param startDate
     *             the top date for this tax to be applied.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Returns the surcharge to be applied.
     * 
     * @return the surcharge.
     * @hibernate.property 
     */
    public double getSurcharge() {
        return surcharge;
    }

    /**
     * Assigns the surcharge to be applied.
     * 
     * @param surcharge
     *            the surcharge to be applied.
     */
    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }

    /**
     * Returns the tax type.
     * 
     * @return tax type.
     *      type = "com.code.aon.product.enumeration.hibernate.TaxTypeUserType"
     */
    @Column(name="tax_type", nullable=false)
    public TaxType getType() {
        return type;
    }

    /**
     * Assigns the tax type.
     * 
     * @param type
     *            tax type.
     */
    public void setType(TaxType type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.code.aon.common.ILookupObject#lookups()
     */
   @Transient
	public Map<String, Object> getLookups() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(IProductAlias.TAX_ID,getId());
		map.put(IProductAlias.TAX_NAME,getName());
		map.put(IProductAlias.TAX_START_DATE,getStartDate());
		map.put(IProductAlias.TAX_PERCENTAGE,new Double(getPercentage()));
		map.put(IProductAlias.TAX_SURCHARGE,new Double(getSurcharge()));
		return map;
	}
}