package com.code.aon.sales;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.PayMethod;
import com.code.aon.customer.Customer;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;

/**
 * Transfer Object that represents a Sale.
 * 
 * @author jurkiri
 */
@Entity
@Table(name="sales")
public class Sales implements ITransferObject, IHeaderObject, ICalculableContainer {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(Sales.class.getName());
	
	/**
	 * The Constructor. Sets TODAY to issueDate
	 */
	public Sales() {
		this.issueDate = new Date();
	}
	
	private static final long serialVersionUID = 2635528648512356470L;

	/** The id. */
    private Integer id;

    /** The serie. */
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
    
    /** The document type. */
    private DocumentType documentType;
    
    /** The security level. */
    private SecurityLevel securityLevel;

    /** The status. */
    private SalesStatus status;
    
	/** The pos. */
	private PointOfSale pos;
	
	/** The detail of this sale. */
	private Set<SalesDetail> lines = new HashSet<SalesDetail>();

	private WorkPlace workPlace;
	
	/**
	 * Gets the id.
	 * 
	 * @return the id
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
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the series.
	 * 
	 * @return the series
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
	@Column(nullable=false)
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
	 * Gets the customer.
	 * 
	 * @return the customer
	 */
	@ManyToOne
	@JoinColumn( name="customer", nullable = false)
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
	 * Gets the PointOfSale.
	 * 
	 * @return the PointOfSale
	 */
	@ManyToOne
	@JoinColumn( name="pos" )
	public PointOfSale getPos() {
		return pos;
	}

	/**
	 * Sets the PointOfSale.
	 * 
	 * @param pos the PointOfSale
	 */
	public void setPos(PointOfSale pos) {
		this.pos = pos;
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
	@OneToMany(mappedBy = "sales", cascade={CascadeType.REMOVE})
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
	
}