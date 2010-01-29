package com.code.aon.sales;

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
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.Seller;

/**
 * Transfer Object that represents a Sale.
 */
@Entity
@Table(name="sales")
public class Sales implements ITransferObject, IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod {
	
	private static final long serialVersionUID = 2635528648512356470L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Sales.class.getName());
	
	/**
	 * The Constructor. Sets TODAY to issueDate
	 */
	public Sales() {
		this.issueDate = new Date();
	}
	
	/** The id. */
    private Integer id;

    /** The series. */
    private String series;

    /** The number. */
    private int number;

    /** The customer. */
    private Customer customer;
    
    /** The shipping address. */
    private RegistryAddress shippingAddress;

    /** The seller. */
    private Seller seller;
    
    /** The discount expression. */
    private DiscountExpression discountExpression;
    
    /** The issue date. */
    private Date issueDate;

    /** The pay method. */
    private PayMethod payMethod;
    
    /** The security level. */
    private SecurityLevel securityLevel;

    /** The status. */
    private SalesStatus status;
    
    /** The document type. */
    private DocumentType documentType;
    
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
	
	/** The detail of this sale. */
	private Set<SalesDetail> lines = new HashSet<SalesDetail>();
	
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
	 * @return the serie
	 */
	@Column(length=5)
	public String getSeries() {
		return series;
	}

	/**
	 * Sets the serie.
	 * 
	 * @param series the serie
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
	 * Gets the shipping address.
	 * 
	 * @return the shipping address
	 */
	@ManyToOne
	@JoinColumn( name="shipping_address" )
	public RegistryAddress getShippingAddress() {
		return shippingAddress;
	}

	/**
	 * Sets the shipping address.
	 * 
	 * @param shippingAddress the shipping address
	 */
	public void setShippingAddress(RegistryAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	/**
	 * Gets the seller.
	 * 
	 * @return the seller
	 */
	@ManyToOne
	@JoinColumn( name="seller" )
	public Seller getSeller() {
		return seller;
	}

	/**
	 * Sets the seller.
	 * 
	 * @param seller the seller
	 */
	public void setSeller(Seller seller) {
		this.seller = seller;
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
	 * Gets the issue date.
	 * 
	 * @return the issue date
	 */
	@Column(name="issue_date")
	public Date getIssueDate() {
		return issueDate;
	}

	/**
	 * Sets the issue date.
	 * 
	 * @param issueDate the issue date
	 */
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
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
	public SalesStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(SalesStatus status) {
		this.status = status;
	}

	/**
	 * Gets the document type.
	 * 
	 * @return the document type
	 */
	@Column(name="document_type")
	public DocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * Sets the document type.
	 * 
	 * @param documentType the document type
	 */
	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
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
	@OneToMany(mappedBy = "sales", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<SalesDetail> getLines() {
		return this.lines;
	}

	/**
	 * Sets the lines.
	 * 
	 * @param lines the lines
	 */
	public void setLines( Set<SalesDetail> lines ) {
		this.lines = lines;
	}

	/**
	 * Gets the date. Necessary to implement <code>ICalculableContainer</code>
	 * 
	 * @return the date
	 */
	@Transient
	public Date getDate() {
		return issueDate;
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
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), getId());
			return salesDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining salesDetail list", e);
		}
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Sales) {
			Sales s = (Sales) obj;
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