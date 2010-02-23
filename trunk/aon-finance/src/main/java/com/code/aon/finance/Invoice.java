package com.code.aon.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Scope;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;

/**
 * Transfer Object that represents an Invoice.
 * 
 * @author Consulting & Development. Iñigo Gayarre - 13-sep-2005
 * @since 1.0
 * @hibernate.class table "invoice"
 */
@Entity
@Table(name = "invoice")
@org.hibernate.annotations.Table( appliesTo = "invoice", indexes =
	{ @Index(name="IDX_SERIES", columnNames={"series","number","type"}),
		@Index(name="IDX_SERIES_NUMBER", columnNames={"series","number"})})
public class Invoice implements ITransferObject, IHeaderObject, ICalculableContainer, ITaxInfo {
	
	private static final long serialVersionUID = 5692053383866684819L;

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(Invoice.class.getName());
	
	/**
	 * The Constructor. Sets TODAY to issueDate.
	 */
	public Invoice() {
		this.issueDate = new Date();
		this.defaultTaxInfo = true;
	}

    /** The id. */
    private Integer id;

    /** The serie. */
    private String series;

    /** The number. */
    private int number;

    /** The reference code. */
    private String referenceCode;

    /** The registry. */
    private Registry registry;
    
    /** The name of the registry. */
    private String registryName;
    
    /** The document of the registry. */
    private String registryDocument;

    /** The registry address. */
    private RegistryAddress registryAddress;
    
    /** The issue date. */
    private Date issueDate;

    /** The tax date. */
    private Date taxDate;

    /** The security level. */
    private SecurityLevel securityLevel;

    /** The status. */
    private InvoiceStatus status;
    
    /** The type. */
    private InvoiceType type;
    
    /** The tax free. */
    private boolean taxFree;

    /** The surcharge. */
    private boolean surcharge;
    
    private boolean withholding;
    
    private String comments;
    
    /** If the Invoice is an investment. */
    private boolean investment;

    /** If the Invoice is an investiment. */
    private InvoiceTransactionType transaction;

    /** If the Invoice is signed. */
    private boolean signed;    

    /** The scope. */
    private Scope scope;

    /** The detail of this invoice. */
	private Set<InvoiceDetail> lines = new HashSet<InvoiceDetail>();

	/** The detail of this invoice. */
	private Set<Finance> finances = new HashSet<Finance>();

	/** The detail of this invoice. */
	private Set<InvoiceAddress> addresses = new HashSet<InvoiceAddress>();

	/** The detail of this invoice. */
	private Set<InvoiceAttachment> attachments = new HashSet<InvoiceAttachment>();

	private int issueYear;
	private int issueMonth;
	private int issueDay;
	private boolean defaultTaxInfo;
	/**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
    @GeneratedValue
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
     * Gets the registry.
     * 
     * @return the registry
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registry", nullable = false)
    @ForeignKey(name="FK_INVOICE_REGISTRY")
    @Index(name="IDX_INVOICE_REGISTRY")  
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Sets the registry.
     * 
     * @param registry the registry
     */
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    /**
     * Gets the registry name.
     * 
     * @return the registry name
     */
    @Column(name="rname", length=128)
    public String getRegistryName() {
		return registryName;
	}

	/**
	 * Sets the registry name.
	 * 
	 * @param registryName the registry name
	 */
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

    /**
     * Gets the registry document.
     * 
     * @return the registry document
     */
	@Column(name="rdocument", length=16)
    public String getRegistryDocument() {
		return registryDocument;
	}

	/**
	 * Sets the registry document.
	 * 
	 * @param registryDocument the registry document
	 */
	public void setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
	}

	/**
	 * Gets the registry address.
	 * 
	 * @return the registry address
	 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="raddress")
    @ForeignKey(name="FK_INVOICE_RADDRESS")
    @Index(name="IDX_INVOICE_RADDRESS") 
    public RegistryAddress getRegistryAddress() {
        return registryAddress;
    }

    /**
     * Sets the registry address.
     * 
     * @param registryAddress the registry address
     */
    public void setRegistryAddress(RegistryAddress registryAddress) {
        this.registryAddress = registryAddress;
    }

    /**
     * Gets the issue date.
     * 
     * @return the issue date
     */
    @Column(name="issue_date")
    @Temporal(TemporalType.DATE)
    @Index(name="IDX_INVOICE_ISSUE_DATE")
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
     * Gets the tax date.
     * 
     * @return the tax date
     */
    @Column(name="tax_date")
    @Temporal(TemporalType.DATE)
    @Index(name="IDX_INVOICE_TAX_DATE")
    public Date getTaxDate() {
        return taxDate;
    }

    /**
     * Sets the tax date.
     * 
     * @param taxDate the tax date
     */
    public void setTaxDate(Date taxDate) {
    	this.taxDate = taxDate;
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
     * Gets the security level.
     * 
     * @return the security level
     */
    @Column(name = "security_level")
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
     * Gets the reference code.
     * 
     * @return the reference code
     */
	@Column(name="reference_code", length=32)
    public String getReferenceCode() {
		return referenceCode;
	}

	/**
	 * Sets the reference code.
	 * 
	 * @param referenceCode the reference code
	 */
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

    /**
     * Gets the status.
     * 
     * @return the status
     */
    @Column(name="status")
    public InvoiceStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the status
     */
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    
    /**
     * Gets the type.
     * 
     * @return the type
     */
    @Column(name = "type")
    public InvoiceType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(InvoiceType type) {
		this.type = type;
	}
	
	/**
	 * Checks if a surcharge has to be applied.
	 * 
	 * @return true, if a surcharge has to be applied.
	 */
	 
	@Column(nullable=true) 
	public boolean isSurcharge() {
		return surcharge;
	}

	/**
	 * Sets if a surcharge has to be applied.
	 * 
	 * @param surcharge if a surcharge has to be applied.
	 */
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}

	/**
	 * Checks if is tax free.
	 * 
	 * @return true, if is tax free
	 */
    @Column(name = "taxFree")
	public boolean isTaxFree() {
		return taxFree;
	}

	/**
	 * Sets the tax free.
	 * 
	 * @param taxFree the tax free
	 */
	public void setTaxFree(boolean taxFree) {
		this.taxFree = taxFree;
	}
	
	@Column(name = "withholding")
	public boolean isWithholding() {
		return withholding;
	}

	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}
	
	@Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name = "investment")
	public boolean isInvestment() {
		return investment;
	}

	public void setInvestment(boolean investment) {
		this.investment = investment;
	}
	
	@Column(name = "transaction")
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

	@Column(nullable = false)
	public boolean isSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	/**
	 * Gets the scope.
	 * 
	 * @return the scope
	 */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="scope", nullable = false)
    @ForeignKey(name="FK_INVOICE_SCOPE")
    @Index(name="IDX_INVOICE_SCOPE") 
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

	@Formula("year(issue_date)")
	public int getIssueYear() {
	 return issueYear;	
	}
	public void setIssueYear(int year) {
		issueYear = year;
	}
	@Formula("month(issue_date)")
	public int getIssueMonth() {
	 return issueMonth;	
	}
	public void setIssueMonth(int month) {
		issueMonth = month;
	}
	
	@Formula("day(issue_date)")
	public int getIssueDay() {
	 return issueDay;	
	}
	public void setIssueDay(int day) {
		issueDay = day;
	}
	
	/**
	 * Gets the lines.
	 * 
	 * @return the lines
	 */
	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<InvoiceDetail> getLines() {
		return this.lines;
	}

	/**
	 * Sets the lines.
	 * 
	 * @param lines the lines
	 */
	public void setLines( Set<InvoiceDetail> lines ) {
		this.lines = lines;
	}

	/**
	 * Gets the finances.
	 * 
	 * @return the finances
	 */
	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<Finance> getFinances() {
		return this.finances;
	}

	/**
	 * Sets the finances.
	 * 
	 * @param lines the finances
	 */
	public void setFinances( Set<Finance> finances ) {
		this.finances = finances;
	}

	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	public Set<InvoiceAddress> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<InvoiceAddress> addresses) {
		this.addresses = addresses;
	}

	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	public Set<InvoiceAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Set<InvoiceAttachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * Gets the date. Necessary to implement <code>ICalculableContainer</code>
	 * 
	 * @return the date
	 */
	@Transient
	public Date getDate() {
		return this.issueDate;
	}
	
	@Transient
	public IAddress getAddress(){
		return(getAddresses().iterator().hasNext()?getAddresses().iterator().next():getRegistryAddress());
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
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			return invoiceDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail list", e);
		}
		return null;
	}

	/**
	 * Gets the ordered detail list. Used in the reports
	 * 
	 * @return the ordered detail list
	 */
	@Transient
	@SuppressWarnings("unchecked")
	public List getOrderedDetailList() {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getId());
			if(getType().equals(InvoiceType.SALES)){
				criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ITEM_PRODUCT_TYPE));
			}
			criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_ID));
			return invoiceDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining invoiceDetail orderedList", e);
		}
		return null;
	}

	/**
	 * Gets the discount expression. Necessary to implement <code>ICalculableContainer</code>
	 * 
	 * @return the discount expression
	 */
	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}
	
	@Transient
	public boolean isRecordable() {
		return getStatus() == InvoiceStatus.PENDING;
	}
	
	@Transient
	public boolean isRecorded() {
		return getStatus() == InvoiceStatus.SCORED;
	}

	@Transient
	public String getDocumentNumber() {
		String documentNumber = (InvoiceType.SALES == getType()) ? "E" : "R";
		if (!StringUtils.isEmpty(getSeries())) {
			documentNumber += "-" + getSeries();
		}
		documentNumber += "-" + StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return documentNumber;
	}

	@Transient
	public boolean isDefaultTaxInfo() {
		return defaultTaxInfo;
	}

	public void setDefaultTaxInfo(boolean defaultTaxInfo) {
		this.defaultTaxInfo = defaultTaxInfo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Invoice) {
			Invoice o = (Invoice) obj;
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