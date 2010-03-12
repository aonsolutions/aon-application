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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.customer.Customer;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;

/**
 * Transfer Object that represents a customer fee.
 * 
 * @author Consulting & Development. Inigo Gayarre - 7-sep-2005
 * @since 1.0
 */
@Entity
@Table(name="customer_fee")
public class CustomerFee implements ITransferObject, ICalculable {

	private static final long serialVersionUID = 113912434021805866L;

    private Integer id;
    private Customer customer;
    private int line;
    private Item item;
    private String description;
    private double quantity;
    private double price;
    private DiscountExpression discountExpression;
    private Date initialDate;
    private Date finalDate;
    private Date billingDate;
    private BillingPeriod period;
    private SecurityLevel securityLevel;
    private WorkPlace workPlace;
    
	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

	@ManyToOne
	@JoinColumn( name="customer", nullable = true, updatable = false )
	@ForeignKey(name="FK_CUSTOMER_FEE_CUSTOMER")
	@Index(name="IDX_CUSTOMER_FEE_CUSTOMER")
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    @Column(nullable=true)
    public int getLine() {
        return line;
    }
    public void setLine(int line) {
        this.line = line;
    }

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="item" )
	@ForeignKey(name="FK_CUSTOMER_FEE_ITEM")
	@Index(name="IDX_CUSTOMER_FEE_ITEM")	
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

    @Column(length=1024)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(precision=15, scale=3)    
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Column(precision=15, scale=3)
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

	@Column(name="discount_expr", length = 32)
	@Type(type="com.code.aon.product.util.DiscountExpressionUserType")
    public DiscountExpression getDiscountExpression() {
        return discountExpression;
    }
    public void setDiscountExpression(DiscountExpression discountExpression) {
        this.discountExpression = discountExpression;
    }

    @Column(name="initial_date")
    @Temporal(TemporalType.DATE)
    public Date getInitialDate() {
        return initialDate;
    }
    public void setInitialDate(Date initialDate) {
        this.initialDate = initialDate;
    }

    @Column(name="final_date")
    @Temporal(TemporalType.DATE)
    public Date getFinalDate() {
        return finalDate;
    }
    public void setFinalDate(Date finalDate) {
        this.finalDate = finalDate;
    }

    @Column(name="billing_date")
	@Temporal(TemporalType.DATE)
    public Date getBillingDate() {
        return billingDate;
    }
    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

	@Column(nullable=true)
    public BillingPeriod getPeriod() {
        return period;
    }
    public void setPeriod(BillingPeriod period) {
        this.period = period;
    }

	@Column(name = "security_level")
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
    
	@ManyToOne
	@JoinColumn( name="workplace", nullable=false)
	@ForeignKey(name="FK_CUSTOMER_FEE_WORKPLACE")
	@Index(name="IDX_CUSTOMER_FEE_WORKPLACE")		
    public WorkPlace getWorkPlace() {
		return workPlace;
	}
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}


    @Transient
	public Month getBillingDateMonth() {
    	if(billingDate != null){
    		Calendar calendar = new GregorianCalendar();
        	calendar.setTime(billingDate);
        	return Month.getMonthByValue(calendar.get(Calendar.MONTH));
    	}
    	return Month.JANUARY;
	}

	@Transient
	public void setBillingDateMonth(Month month) {
		Calendar calendar = new GregorianCalendar();
		calendar.set(getBillingDateYear(), month.getValue(), 1);
		setBillingDate(calendar.getTime());
	}

	@Transient
	public int getBillingDateYear() {
		Calendar calendar = new GregorianCalendar();
		if (billingDate != null) {
	    	calendar.setTime(billingDate);
		} else {
			calendar.setTime(new Date());
		}
		return calendar.get(Calendar.YEAR);
	}

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