package com.code.aon.purchase;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.code.aon.common.IHeaderObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.PayMethod;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.supplier.Supplier;

/**
 * Transfer Object that represents a purchase.
 * 
 * @author Joseba Urkiri
 */
@Entity
@Table(name="purchase")
public class Purchase implements ITransferObject, IHeaderObject{
	
	/** The id. */
	private Integer id;
	
	/** Purchase's serie. */
	private String series;

    /** Purchase's number. */
    private int number;
	
	/** Purchase's supplier. */
	private Supplier supplier;
	
	/** The issue date. */
	private Date issueDate;
	
	/** The pay method. */
	private PayMethod payMethod;

	/** The security level. */
	private SecurityLevel securityLevel;
	
	/** The status. */
	private PurchaseStatus status;
	
	/** The discount expression that will be applied. */
	private DiscountExpression discountExpression;
	
	/** The document type. */
	private PurchaseDocumentType documentType;

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
	@Column(name="series", nullable=false, length=5)
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
	@Column(name="number", nullable=false)
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
	 * Gets the supplier.
	 * 
	 * @return the supplier
	 */
	@ManyToOne
	@JoinColumn( name="supplier", nullable = false)
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * Sets the supplier.
	 * 
	 * @param supplier the supplier
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
	@JoinColumn( name="pay_method",updatable=false )
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
	@Column(name="status")
	public PurchaseStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(PurchaseStatus status) {
		this.status = status;
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
	 * Gets the document type.
	 * 
	 * @return the document type
	 */
	@Column(name="document_type")
	public PurchaseDocumentType getDocumentType() {
		return documentType;
	}

	/**
	 * Sets the document type.
	 * 
	 * @param documentType the document type
	 */
	public void setDocumentType(PurchaseDocumentType documentType) {
		this.documentType = documentType;
	}

    @ManyToOne
    @JoinColumn(name="workplace", nullable = false)
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
}