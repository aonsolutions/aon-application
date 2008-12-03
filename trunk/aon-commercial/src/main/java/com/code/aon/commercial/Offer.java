package com.code.aon.commercial;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import org.hibernate.annotations.Type;

import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.PayMethod;
import com.code.aon.product.Tariff;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.sales.Seller;

/**
 * Transfer Object that represents a Offer.
 */
@Entity
@Table(name="offer")
public class Offer implements ITransferObject, IHeaderObject, ICalculableContainer {
	
	private static final long serialVersionUID = 851446217271328802L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Offer.class.getName());
	
	/**
	 * The Constructor. Sets TODAY to issueDate
	 */
	public Offer() {
		this.issueDate = new Date();
	}
	
    /** The id. */
    private Integer id;

    /** The serie. */
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
    
	/** The detail of this offer. */
	private Set<OfferDetail> lines = new HashSet<OfferDetail>();

	private WorkPlace workPlace;

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
	 * Gets the discount expression to be applied.
	 * 
	 * @return the discount expression
	 */
	@Column(name="discount_expr")
	@Type(type="com.code.aon.product.util.DiscountExpressionUserType")
	public DiscountExpression getDiscountExpression() {
		return discountExpression;
	}

	/**
	 * Sets the discount expression to be applied.
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
	
    @ManyToOne
    @JoinColumn(name="workplace", nullable = false)
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	/**
	 * Gets the lines.
	 * 
	 * @return the lines
	 */
	@OneToMany(mappedBy = "offer", cascade={CascadeType.REMOVE})
	@OrderBy("id")
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
			LOGGER.log(Level.SEVERE, "Error obtaining offerDetail list", e);
		}
		return null;
	}
	
	/**
	 * Gets the ordered detail list. Used in the reports
	 * 
	 * @return the ordered detail list
	 */
//	@Transient
//	@SuppressWarnings("unchecked")
//	public List getOrderedDetailList() {
//		try {
//			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), getId());
//			criteria.addOrder(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ITEM_PRODUCT_TYPE));
//			criteria.addOrder(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ID));
//			return offerDetailBean.getList(criteria);
//		} catch (ManagerBeanException e) {
//			LOGGER.log(Level.SEVERE, "Error obtaining offerDetail list", e);
//		}
//		return null;
//	}
}