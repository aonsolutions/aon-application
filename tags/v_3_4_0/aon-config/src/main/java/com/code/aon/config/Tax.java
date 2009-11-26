package com.code.aon.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.enumeration.TaxType;

/**
 * Transfer Object that represents a tax.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="tax")
public class Tax implements ITransferObject{

	private static final long serialVersionUID = -513647182781441011L;

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
     * Sales account. 
     */
    private Account salesAccount;

    /**
     * Purchase account. 
     */
    private Account purchaseAccount;

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
     * Returns the tax type.
     * 
     * @return tax type.
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
	 * Returns the sales account
	 * 
     * @return String the sales account.
     * 
     */
	@Transient
	public Account getSalesAccount() {
		return salesAccount;
	}

    /**
     * Assigns the sales account.
     * 
     * @param salesAccount
     *            the sales account.
     */
	@Transient
	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}

    /**
	 * Returns the purchase account
	 * 
     * @return String the purchase account.
     * 
     */
	@Transient
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}

    /**
     * Assigns the purchase account.
     * 
     * @param purchaseAccount
     *            the purchase account.
     */
	@Transient
	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Tax) {
			Tax o = (Tax) obj;
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