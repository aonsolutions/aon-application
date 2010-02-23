package com.code.aon.commercial;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.Tariff;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.seller.Seller;

/**
 * Transfer Object that represents a Offer.
 */
@Entity
@Table(name="offer")
public class Offer implements ITransferObject, IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod {
	
	private static final long serialVersionUID = 851446217271328802L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(Offer.class.getName());
	
	/**
	 * The Constructor. Sets TODAY to issueDate
	 */
	public Offer() {
		this.issueDate = new Date();
	}
	
    /** The id. */
    private Integer id;

    /** The series. */
    private String series;

    /** The number. */
    private int number;

    /** The target. */
    private Target target;
    
    /** The address. */
    private RegistryAddress address;

    /** The tariff. */
    private Tariff tariff;

    /** The seller. */
    private Seller seller;
    
    /** The discount expression to be applied. */
    private DiscountExpression discountExpression;
    
    /** The issue date. */
    private Date issueDate;

    /** The pay method. */
    private PayMethod payMethod;
    
    /** The security level. */
    private SecurityLevel securityLevel;

    /** The status of the offer. */
    private OfferStatus status;
    
    /** The offer type. */
    private OfferType type;
    
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
	
	/** The comments. */
	private String comments;
	
    /** If the Offer is signed. */
    private boolean signed;    
	
	/** The detail of this offer. */
	private Set<OfferDetail> lines = new HashSet<OfferDetail>();
	
	/** The attachemnts of this offer. */
	private Set<OfferAttachment> attachments = new HashSet<OfferAttachment>();	

	/** The terms of this offer. */
	private Set<OfferTerm> terms = new HashSet<OfferTerm>();	
	
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
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

	/**
	 * Gets the target.
	 * 
	 * @return the target
	 */
	@ManyToOne
	@JoinColumn( name="target", nullable = false )
	public Target getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 * 
	 * @param target the target
	 */
	public void setTarget(Target target) {
		this.target = target;
	}

	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	@ManyToOne
	@JoinColumn( name="address" )
	public RegistryAddress getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 * 
	 * @param address the address
	 */
	public void setAddress(RegistryAddress address) {
		this.address = address;
	}

	/**
	 * Gets the tariff.
	 * 
	 * @return the tariff
	 */
	@ManyToOne
	@JoinColumn( name="tariff" )
	public Tariff getTariff() {
		return tariff;
	}

	/**
	 * Sets the tariff.
	 * 
	 * @param tariff the tariff
	 */
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
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
	public OfferStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(OfferStatus status) {
		this.status = status;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public OfferType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(OfferType type) {
		this.type = type;
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
	@OneToMany(mappedBy = "offer", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<OfferDetail> getLines() {
		return this.lines;
	}

	/**
	 * Sets the lines.
	 * 
	 * @param lines the lines
	 */
	public void setLines( Set<OfferDetail> lines ) {
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
	 * Gets the comments.
	 * 
	 * @return the comments
	 */
	@Lob
	@Column(name="comments")	
	@Type(type="stringClob")
	public String getComments() {
		return comments;
	}

	/**
	 * Sets the comments.
	 * 
	 * @param comments the new comments
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	/**
	 * Checks if is signed.
	 * 
	 * @return true, if is signed
	 */
	@Column(nullable = false)
	public boolean isSigned() {
		return signed;
	}

	/**
	 * Sets the signed.
	 * 
	 * @param signed the new signed
	 */
	public void setSigned(boolean signed) {
		this.signed = signed;
	}	

	/**
	 * Gets the attachments.
	 * 
	 * @return the attachments
	 */
	@OneToMany(mappedBy = "offer", cascade={CascadeType.REMOVE})
	public Set<OfferAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * Sets the attachments.
	 * 
	 * @param attachments the new attachments
	 */
	public void setAttachments(Set<OfferAttachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * Gets the terms.
	 * 
	 * @return the terms
	 */
	@OneToMany(mappedBy = "offer", cascade={CascadeType.REMOVE})
	public Set<OfferTerm> getTerms() {
		return terms;
	}

	/**
	 * Sets the terms.
	 * 
	 * @param terms the new terms
	 */
	public void setTerms(Set<OfferTerm> terms) {
		this.terms = terms;
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
			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), getId());
			return offerDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining offerDetail list", e);
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Offer o = (Offer) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.address, o.address)				
				.append(this.bank, o.bank)
				.append(this.bankAccount, o.bankAccount)				
				.append(this.comments, o.comments)
				.append(this.daysBetweenPayments, o.daysBetweenPayments)				
				.append(this.daysToFirstPayment, o.daysToFirstPayment)
				.append(this.discountExpression, o.discountExpression)				
				.append(this.issueDate, o.issueDate)				
				.append(this.number, o.number)
				.append(this.numberOfPayments, o.numberOfPayments)				
				.append(this.paymentDays, o.paymentDays)
				.append(this.payMethod, o.payMethod)				
				.append(this.scope, o.scope)
				.append(this.securityLevel, o.securityLevel)				
				.append(this.seller, o.seller)				
				.append(this.series, o.series)
				.append(this.signed, o.signed)				
				.append(this.status, o.status)
				.append(this.target, o.target)				
				.append(this.tariff, o.tariff)
				.append(this.type, o.type)				
				.append(this.workPlace, o.workPlace)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(address)
			.append(bank)
			.append(bankAccount)
			.append(comments)	
			.append(daysBetweenPayments)
			.append(daysToFirstPayment)
			.append(discountExpression)
			.append(id)			
			.append(issueDate)
			.append(number)
			.append(numberOfPayments)
			.append(paymentDays)
			.append(payMethod)
			.append(scope)
			.append(securityLevel)
			.append(seller)
			.append(series)
			.append(signed)
			.append(status)
			.append(target)
			.append(tariff)
			.append(type)
			.append(workPlace)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}