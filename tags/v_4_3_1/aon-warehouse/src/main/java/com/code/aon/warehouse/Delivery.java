package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Transfer Object that represents a Delivery.
 */
@Entity
@Table(name="delivery")
public class Delivery implements ITransferObject, IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod {
	
	private static final long serialVersionUID = 5865460388758611455L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Delivery.class.getName());
	
	/**
	 * The Constructor. Sets TODAY to issueDate
	 */
	public Delivery() {
		this.issueTime = new Date();
	}
	
	/** The id. */
	private Integer id;
	
	/** The series. */
    private String series;

	/** The number. */
    private int number;
	
	/** The customer. */
	private Customer customer;
	
	/** The address. */
	private RegistryAddress raddress;
	
	/** The issue date. */
	private Date issueTime;
	
	/** The pay method. */
	private PayMethod payMethod;
	
	/** The security level. */
	private SecurityLevel securityLevel;
	
	/** The status. */
	private DeliveryStatus status;
	
    /** The workplace. */
	private WorkPlace workPlace;

    /** The scope. */
	private Scope scope;

    /** The number of payments. */
    private int numberOfPayments;

    /** The days to first payment. */
    private int daysToFirstPayment;

    /** The days between payments. */
    private int daysBetweenPayments;

    /** The payment days. */
    private String paymentDays;

    private int[] paymentDaysArray;

	/** The bank. */
	private Bank bank;
	
	/** The bank account. */
	private BankAccount bankAccount;
	
	/** The detail of this delivery. */
	private Set<DeliveryDetail> lines = new HashSet<DeliveryDetail>();
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the serie.
	 * 
	 * @return the series
	 */
	@Column(length=5)
	public String getSeries() {
		return series;
	}

	/**
	 * Sets the series.
	 * 
	 * @param series the series
	 */
	public void setSeries(String series) {
		this.series = series;
	}
	
	/**
	 * Gets the number.
	 * 
	 * @return the number
	 */
	@Column(nullable = false)
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the number.
	 * 
	 * @param number the number
	 */
	public void setNumber(int number) {
		this.number = number;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = "" + getNumber();
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

	/**
	 * Gets the customer.
	 * 
	 * @return the customer
	 */
	@ManyToOne
	@JoinColumn( name="customer", nullable = false )
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
	 * Gets the address
	 * 
	 * @return the address.
	 */
	@ManyToOne
	@JoinColumn( name="address" )
	public RegistryAddress getRaddress() {
		return raddress;
	}

	/**
	 * Sets the address.
	 * 
	 * @param address the address
	 */
	public void setRaddress(RegistryAddress raddress) {
		this.raddress = raddress;
	}

	/**
	 * Gets the issue time.
	 * 
	 * @return the issue time.
	 */
	@Column(name="issue_time")
	public Date getIssueTime() {
		return issueTime;
	}

	/**
	 * Sets the issue time.
	 * 
	 * @param issueTime the issue time
	 */
	public void setIssueTime(Date issueTime) {
		this.issueTime = issueTime;
	}

	/**
	 * Gets the pay method.
	 * 
	 * @return the pay method
	 */
	@ManyToOne
	@JoinColumn( name="pay_method" )
	public PayMethod getPayMethod() {
		return payMethod;
	}

	/**
	 * Sets the pay method.
	 * 
	 * @param payMethod the pay method
	 */
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	/**
	 * Gets the security level.
	 * 
	 * @return the security level
	 */
	@Column(name="security_level")
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

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public DeliveryStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(DeliveryStatus status) {
		this.status = status;
	}

	/**
	 * Gets the workplace.
	 * 
	 * @return the workplace
	 */
    @ManyToOne
    @JoinColumn(name="workplace", nullable = false)
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	/**
	 * Sets the workplace.
	 * 
	 * @param workplace the workplace
	 */
	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	/**
	 * Gets the scope.
	 * 
	 * @return the scope
	 */
    @ManyToOne
    @JoinColumn(name="scope", nullable = false)
	public Scope getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 * 
	 * @param scope the scope
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
    /**
     * Gets the number of payments.
     * 
     * @return the number of payments
     */
    @Column(name = "number_of_pymnts")
    public int getNumberOfPayments() {
        return numberOfPayments;
    }

    /**
     * Sets the number of payments.
     * 
     * @param numberOfPayments the number of payments
     */
    public void setNumberOfPayments(int numberOfPayments) {
        this.numberOfPayments = numberOfPayments;
    }
    
    /**
     * Gets the days to first payment.
     * 
     * @return the days to first payment
     */
    @Column(name = "days_to_first_pymnt")
    public int getDaysToFirstPayment() {
        return daysToFirstPayment;
    }

    /**
     * Sets the days to first payment.
     * 
     * @param daysToFirstPayment the days to first payment
     */
    public void setDaysToFirstPayment(int daysToFirstPayment) {
        this.daysToFirstPayment = daysToFirstPayment;
    }

    /**
     * Gets the days between payments.
     * 
     * @return the days between payments
     */
    @Column(name = "days_between_pymnts")
    public int getDaysBetweenPayments() {
        return daysBetweenPayments;
    }

    /**
     * Sets the days between payments.
     * 
     * @param daysBetweenPayment the days between payments
     */
    public void setDaysBetweenPayments(int daysBetweenPayment) {
        this.daysBetweenPayments = daysBetweenPayment;
    }

    /**
     * Gets the payment days.
     * 
     * @return the payment days
     */
    @Column(name="pymnt_days", length=8)
    public String getPaymentDays() {
        return paymentDays;
    }
    
    /** The DELIM. */
    private final String DELIM = " ";
    
    /**
     * Sets the payment days.
     * 
     * @param paymentDays the payment days
     */
    public void setPaymentDays(String paymentDays) {
        this.paymentDays = paymentDays;
        StringTokenizer strTknzr = new StringTokenizer(this.paymentDays,DELIM);
    	int[] values = new int[strTknzr.countTokens()];
    	for (int i = 0; i < values.length; i++){
    		values[i] = Integer.parseInt(strTknzr.nextToken());
    	}    	
        this.paymentDaysArray = values;
    }

    @Transient
    public int[] getPaymentDaysArray() {
    	return paymentDaysArray;
    }

	/**
	 * Gets the bank.
	 * 
	 * @return the bank
	 */
	@ManyToOne
    @JoinColumn(name="bank")
	public Bank getBank() {
		return bank;
	}

	/**
	 * Sets the bank.
	 * 
	 * @param bank the bank
	 */
	public void setBank(Bank bank) {
		this.bank = bank;
	}

	/**
	 * Gets the bank account.
	 * 
	 * @return the bank account
	 */
	@Column(name="bank_account", length=30)
	@Type(type="com.code.aon.config.hibernate.BankAccountType")
	public BankAccount getBankAccount() {
		return bankAccount;
	}

	/**
	 * Sets the bank account.
	 * 
	 * @param bankAccount the bank account
	 */
	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	/**
	 * Gets the lines.
	 * 
	 * @return the lines
	 */
	@OneToMany(mappedBy = "delivery", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<DeliveryDetail> getLines() {
		return this.lines;
	}

	/**
	 * Sets the lines.
	 * 
	 * @param lines the lines
	 */
	public void setLines( Set<DeliveryDetail> lines ) {
		this.lines = lines;
	}
	
	/**
	 * Gets the date. Necessary to implement <code>ICalculableContainer</code>
	 * 
	 * @return the date
	 */
	@Transient
	public Date getDate() {
		return this.issueTime;
	}

	/**
	 * Gets the discountExpression. Necessary to implement <code>ICalculableContainer</code>
	 * 
	 * @return the discount expression
	 */
	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

	/**
	 * Gets the payMethod. Necessary to implement <code>IPayMethod</code>
	 * 
	 * @return the payMethod
	 */
	@Transient
	public PayMethod getPayment() {
		return payMethod;
	}

	/**
	 * Gets the detail list. Used in the reports
	 * 
	 * @return the detail list
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
	 * Gets the detail list ordered. Used in the reports
	 * 
	 * @return the detail list
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Delivery) {
			Delivery s = (Delivery) obj;
			if (s.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), s.getId())) {
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