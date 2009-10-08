package com.code.aon.finance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.finance.enumeration.BillingPeriod;

/**
 * Transfer Object that represents a customer fee.
 * 
 * @author Consulting & Development. Inigo Gayarre - 7-sep-2005
 * @since 1.0
 */
@Entity
@Table(name="customer_fee")
public class CustomerFee implements ITransferObject, ICalculable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 113912434021805866L;

	/** The id. */
    private Integer id;

    /** The Customer. */
    private Customer customer;

    /** The Item. */
    private Item item;

    /** The Description. */
    private String description;

    /** The Quantity. */
    private double quantity;

    /** The Price of the item. */
    private double price;

    /** Aritmethical expression representing the discounts to be applied. */
    private DiscountExpression discountExpression;

    /** The Initial date. */
    private Date initialDate;

    /** The Final date. */
    private Date finalDate;

    /** The Billing date. */
    private Date billingDate;

    /** The Billing period. */
    private BillingPeriod period;
    
    /** The Security level. */
    private SecurityLevel securityLevel;
    
    private WorkPlace workPlace;
    
    /**
     * Gets the id.
     * 
     * @return Returns the id
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
	 * @param id The id
	 */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the billing date.
     * 
     * @return the billing date
     */
	@Column(name="billing_date")
    public Date getBillingDate() {
        return billingDate;
    }

    /**
     * Sets the billing date.
     * 
     * @param billingDate the billing date
     */
    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * 
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

	/**
	 * Gets the discount expression.
	 * 
	 * @return the discount expression
	 */
	@Column(name="discount_expr")
	@Type(type="com.code.aon.product.util.DiscountExpressionUserType")
    public DiscountExpression getDiscountExpression() {
        return discountExpression;
    }

    /**
     * Sets the discount expression.
     * 
     * @param discountExpression the discount expression
     */
    public void setDiscountExpression(DiscountExpression discountExpression) {
        this.discountExpression = discountExpression;
    }

    /**
     * Gets the final date.
     * 
     * @return the final date
     */
    @Column(name="final_date")
    public Date getFinalDate() {
        return finalDate;
    }

    /**
     * Sets the final date.
     * 
     * @param finalDate the final date
     */
    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    /**
     * Gets the initial date.
     * 
     * @return the initial date
     */
    @Column(name="initial_date")
    public Date getInitialDate() {
        return initialDate;
    }

    /**
     * Sets the initial date.
     * 
     * @param initialDate the initial date
     */
    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="item" )
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
	 * Gets the period.
	 * 
	 * @return the period
	 */
	@Column(nullable=false)
    public BillingPeriod getPeriod() {
        return period;
    }

    /**
     * Sets the period.
     * 
     * @param period the period
     */
    public void setPeriod(BillingPeriod period) {
        this.period = period;
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
	 * Gets the customer.
	 * 
	 * @return the customer
	 */
	@ManyToOne
	@JoinColumn( name="customer", nullable = true, updatable = false )
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer.
     * 
     * @param customer the customer
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
	/**
	 * Gets the security level.
	 * 
	 * @return the security level
	 */
	@Column(name = "security_level", nullable=false)
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    /**
     * Sets the security level.
     * 
     * @param securityLevel the security level
     */
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
    
	@ManyToOne
	@JoinColumn( name="workplace", nullable=false)
    public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/**
     * Gets the billing date month.
     * 
     * @return the billing date month
     */
    @Transient
	public Month getBillingDateMonth() {
    	if(billingDate != null){
    		Calendar calendar = new GregorianCalendar();
        	calendar.setTime(billingDate);
        	return Month.getMonthByValue(calendar.get(Calendar.MONTH));
    	}
    	return Month.JANUARY;
	}

	/**
	 * Sets the billing date month.
	 * 
	 * @param billingdateMonth the billing date month
	 */
	@Transient
	public void setBillingDateMonth(Month month) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(getBillingDateYear(), month.getValue(), 1);
		setBillingDate(calendar.getTime());
	}

	/**
	 * Gets the billing date year.
	 * 
	 * @return the billing date year
	 */
	@Transient
	public int getBillingDateYear() {
		Calendar calendar = new GregorianCalendar();
		if(billingDate != null){
	    	calendar.setTime(billingDate);
		}else{
			calendar.setTime(new Date());
		}
		return calendar.get(Calendar.YEAR);
	}

	/**
	 * Sets the billing date year.
	 * 
	 * @param billingdateYear the billing date year
	 */
	@Transient
	public void setBillingDateYear(int billingDateYear) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(billingDateYear, getBillingDateMonth().getValue(), 1);
		setBillingDate(calendar.getTime());
	}

	@Override
	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof CustomerFee) {
			CustomerFee o = (CustomerFee) obj;
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