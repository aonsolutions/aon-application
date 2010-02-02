package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.customer.Customer;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Transfer Object that represents a Delivery.
 * 
 * @author igayarre
 *
 */
@Entity
@Table(name="delivery")
public class Delivery implements ITransferObject, IHeaderObject, ICalculableContainer{
	
	/**
	 * The logger of this class
	 */
	private static final Logger LOGGER = Logger.getLogger(Delivery.class.getName());
	
	/**
	 * Unique key
	 */
	private Integer id;
	
    /**
     * The series
     */
    private String series;

    /**
     * The numer of this series
     */
    private int number;
	
	/**
	 * Customer related to this delivery
	 */
	private Customer customer;
	
	/**
	 * The address
	 */
	private RegistryAddress raddress;
	
	/**
	 * The date and time
	 */
	private Date issueTime;
	
	/**
	 * Security level
	 */
	private SecurityLevel securityLevel;
	
	/**
	 * The delivery status 
	 */
	private DeliveryStatus status;
	
	/**
	 * All the lines of this delivery
	 */
	private Set<DeliveryDetail> lines = new HashSet<DeliveryDetail>();
	
	/**
	 * Constructor with default date, now
	 */
	public Delivery() {
		this.issueTime = new Date();
	}
	
	/**
	 * Returns the unique key
	 * 
	 * @return unique key
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	
	/**
	 * Assigns the unique key
	 * 
	 * @param primaryKey
	 */
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	/**
	 * Retruns the number
	 * 
	 * @return the number.
	 */
	@Column(nullable=false)
	public int getNumber() {
		return number;
	}
	/**
	 * Assigns the number
	 * 
	 * @param number The number to set.
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	/**
	 * Returns the series.
	 * 
	 * @return the series.
	 */
	@Column(length=5)
	public String getSeries() {
		return series;
	}
	/**
	 * Assigns the series
	 * 
	 * @param series The series to set.
	 */
	public void setSeries(String series) {
		this.series = series;
	}
	/**
	 * Returns the customer
	 * 
	 * @return the customer
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="customer",nullable=false )
	public Customer getCustomer() {
		return customer;
	}
	/**
	 * Assigns the customer
	 * 
	 * @param customer The customer to set.
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	/**
	 * Returns the issueTime
	 * 
	 * @return the issueTime.
	 */
	@Column(name="issue_time")
	public Date getIssueTime() {
		return issueTime;
	}
	/**
	 * Assigns the issue time
	 * 
	 * @param issueTime The issueTime to set.
	 */
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}
	/**
	 * Returns the raddress
	 * 
	 * @return Returns the raddress.
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="address" )
	public RegistryAddress getRaddress() {
		return raddress;
	}
	/**
	 * Assigns the raddress
	 * 
	 * @param raddress The raddress to set.
	 */
	public void setRaddress(RegistryAddress raddress) {
		this.raddress = raddress;
	}
	/**
	 * Returns the securityLevel.
	 * 
	 * @return the securityLevel.
	 */
	@Column(name="security_level",nullable=false)
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	/**
	 * Assigns the security level
	 * 
	 * @param securityLevel The securityLevel to set.
	 */
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
	/**
	 * Returns the status.
	 * 
	 * @return the status.
	 */
	@Column(nullable=false)
	public DeliveryStatus getStatus() {
		return status;
	}
	/**
	 * Assigns the status
	 * 
	 * @param status The status to set.
	 */
	public void setStatus(DeliveryStatus status) {
		this.status = status;
	}
	
	/**
	 * Returns the DeliveryDetail set
	 * 
	 * @return a DeliveryDetail Set
	 */
	@OneToMany(mappedBy = "delivery", cascade={CascadeType.REMOVE})
	public Set<DeliveryDetail> getLines() {
		return this.lines;
	}

	/**
	 * Assigns the DeliveryDetail set
	 * 
	 * @param lines DeliveryDetail set
	 */
	public void setLines( Set<DeliveryDetail> lines ) {
		this.lines = lines;
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.ICalculableContainer#getDate()
	 */
	@Transient
	public Date getDate() {
		return this.issueTime;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.ICalculableContainer#getDetailList()
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public List getDetailList() {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), getId());
			return deliveryDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining deliveryDetail list", e);
		}
		return null;
	}
	
	/**
	 * Returns the DeliveryDetail list for this Delivery
	 * 
	 * @return a list of DeliveryDetail
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public List getOrderedDetailList() {
		try {
			IManagerBean offerDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), getId());
			criteria.addOrder(offerDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ITEM_PRODUCT_TYPE));
			criteria.addOrder(offerDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_ID));
			return offerDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining offerDetail list", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.product.strategy.ICalculableContainer#getDiscountExpression()
	 */
	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

    @Override
    public boolean equals(Object obj) {
    	if(id == null){
    		return super.equals(obj);
    	}
        if (obj instanceof Delivery) {
            return (this.id.equals(((Delivery)obj).getId()));
        }
        return false;
    }

}