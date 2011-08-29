package com.code.aon.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IAddress;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.registry.enumeration.DocumentType;

@Entity
@Table(name = "invoice")
@org.hibernate.annotations.Table( appliesTo = "invoice", indexes =
	{@Index(name="IDX_SERIES", columnNames={"series","number","type"}),
		@Index(name="IDX_SERIES_NUMBER", columnNames={"series","number"})})
public class Invoice implements ITransferObject, IHeaderObject, ICalculableContainer, ITaxInfo, IConfidentialable, IScopable {
	
	private static final long serialVersionUID = 5692053383866684819L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Invoice.class.getName());
	
    private Integer id;
    private String series;
    private int number;
    private String referenceCode;
    private Project project;
    private Registry registry;
    private String registryName;
    private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
    private RegistryAddress registryAddress;
    private Date issueDate;
    private Date taxDate;
    private SecurityLevel securityLevel;
    private InvoiceStatus status;
    private InvoiceType type;
    private boolean taxFree;
    private boolean surcharge;
    private boolean withholding;
    private String comments;
    private boolean investment;
    private InvoiceTransactionType transaction;
    private boolean signed;    
    private Scope scope;
    private boolean service;    
    private RectificationType rectificationType;
    private Invoice rectificationInvoice;
    private double taxableBase;
    private double vatQuota;
    private double retentionQuota;
    private double total;

	private int issueYear;
	private int issueMonth;
	private int issueDay;
	private boolean defaultTaxInfo;
	private boolean updateEnabled;

	private Set<InvoiceDetail> lines = new HashSet<InvoiceDetail>();
	private Set<Finance> finances = new HashSet<Finance>();
	private Set<InvoiceAddress> addresses = new HashSet<InvoiceAddress>();
	private Set<InvoiceAttachment> attachments = new HashSet<InvoiceAttachment>();

	public Invoice() {
		this.issueDate = new Date();
		this.defaultTaxInfo = true;
		this.updateEnabled = true;
	}

    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(length=5)
    public String getSeries() {
        return series;
    }
    public void setSeries(String series) {
        this.series = series;
    }

    @Column(nullable = false)
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

	@Column(name="reference_code", length=32)
    public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="project")
    @ForeignKey(name="FK_INVOICE_PROJECT")
    @Index(name="IDX_INVOICE_PROJECT")  
    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registry", nullable = false)
    @ForeignKey(name="FK_INVOICE_REGISTRY")
    @Index(name="IDX_INVOICE_REGISTRY")  
    public Registry getRegistry() {
        return registry;
    }
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }
    
    @Column(name="rname", length=128)
    public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}

	@Column(name="rdocument", length=16)
    public String getRegistryDocument() {
		return registryDocument;
	}
	public void setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
	}

	@Column(name="rdocument_type")
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public void setRegistryDocumentType(DocumentType registryDocumentType) {
		this.registryDocumentType = registryDocumentType;
	}
	
	@Column(name="rdocument_country")
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.common.enumeration.Country") })
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public void setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.registryDocumentCountry = registryDocumentCountry;
	}

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="raddress")
    @ForeignKey(name="FK_INVOICE_RADDRESS")
    @Index(name="IDX_INVOICE_RADDRESS") 
    public RegistryAddress getRegistryAddress() {
        return registryAddress;
    }
    public void setRegistryAddress(RegistryAddress registryAddress) {
        this.registryAddress = registryAddress;
    }

    @Column(name="issue_date")
    @Temporal(TemporalType.DATE)
    @Index(name="IDX_INVOICE_ISSUE_DATE")
    public Date getIssueDate() {
        return issueDate;
    }
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Column(name="tax_date")
    @Temporal(TemporalType.DATE)
    @Index(name="IDX_INVOICE_TAX_DATE")
    public Date getTaxDate() {
        return taxDate;
    }
    public void setTaxDate(Date taxDate) {
    	this.taxDate = taxDate;
    }

    @Column(name = "security_level")
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    public InvoiceStatus getStatus() {
        return status;
    }
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    
    public InvoiceType getType() {
		return type;
	}
	public void setType(InvoiceType type) {
		this.type = type;
	}
	
	public boolean isTaxFree() {
		return taxFree;
	}
	public void setTaxFree(boolean taxFree) {
		this.taxFree = taxFree;
	}
	
	@Column(nullable=true) 
	public boolean isSurcharge() {
		return surcharge;
	}
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}

	public boolean isWithholding() {
		return withholding;
	}
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}
	
	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	public boolean isInvestment() {
		return investment;
	}
	public void setInvestment(boolean investment) {
		this.investment = investment;
	}
	
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="scope", nullable = false)
    @ForeignKey(name="FK_INVOICE_SCOPE")
    @Index(name="IDX_INVOICE_SCOPE") 
    public Scope getScope() {
        return scope;
    }
    public void setScope(Scope scope) {
        this.scope = scope;
    }

	@Column(nullable = false)
	public boolean isService() {
		return service;
	}
	public void setService(boolean service) {
		this.service = service;
	}

    @Column(name = "rectification_type")
    public RectificationType getRectificationType() {
		return rectificationType;
	}
	public void setRectificationType(RectificationType type) {
		this.rectificationType = type;
	}
	
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rectification_invoice")
    @ForeignKey(name="FK_INVOICE_INVOICE")
    @Index(name="IDX_INVOICE_INVOICE") 
    public Invoice getRectificationInvoice() {
        return rectificationInvoice;
    }
    public void setRectificationInvoice(Invoice invoice) {
        this.rectificationInvoice = invoice;
    }

    @Column(name="taxable_base")
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	
    @Column(name="vat_quota")
	public double getVatQuota() {
		return vatQuota;
	}
	public void setVatQuota(double vatQuota) {
		this.vatQuota = vatQuota;
	}
	
    @Column(name="retention_quota")
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public void setRetentionQuota(double retentionQuota) {
		this.retentionQuota = retentionQuota;
	}
	
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	
	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<InvoiceDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<InvoiceDetail> lines) {
		this.lines = lines;
	}

	@OneToMany(mappedBy = "invoice", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<Finance> getFinances() {
		return this.finances;
	}
	public void setFinances(Set<Finance> finances) {
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
	
	@Transient
	public boolean isDefaultTaxInfo() {
		return defaultTaxInfo;
	}
	public void setDefaultTaxInfo(boolean defaultTaxInfo) {
		this.defaultTaxInfo = defaultTaxInfo;
	}

	@Transient
	public boolean isUpdateEnabled() {
		return updateEnabled;
	}
	public void setUpdateEnabled(boolean updateEnabled) {
		this.updateEnabled = updateEnabled;
	}

	@Transient
	public Date getDate() {
		return this.issueDate;
	}
	
	@Transient
	public IAddress getAddress(){
		return(getAddresses().iterator().hasNext()?getAddresses().iterator().next():getRegistryAddress());
	}
	
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

	@Transient
	public String getDocumentNumber() {
		return FinanceUtil.getDocumentNumber(getType(), getSeries(), getNumber());
	}

	@Transient
	public boolean isValidRegistryDocument() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getRegistryDocument());
		registryDocument.setType(getRegistryDocumentType());
		registryDocument.setCountry(getRegistryDocumentCountry());
		return registryDocument.isValid();
	}
	@Transient
	public boolean isRegistryDocumentValidable() {
		RegistryDocument registryDocument = new RegistryDocument();
		registryDocument.setDocument(getRegistryDocument());
		registryDocument.setType(getRegistryDocumentType());
		registryDocument.setCountry(getRegistryDocumentCountry());
		return registryDocument.isValidable();
	}
	
	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

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
	public boolean isSales() {
		return getType() == InvoiceType.SALES;
	}
	@Transient
	public boolean isPurchase() {
		return getType() == InvoiceType.PURCHASE;
	}
	@Transient
	public boolean isExpense() {
		return getType() == InvoiceType.EXPENSES;
	}
	@Transient
	public boolean isUndeductibleExpense() {
		return getType() == InvoiceType.UNDEDUCTIBLE;
	}
	@Transient
	public boolean isNational() {
		return getTransaction() == InvoiceTransactionType.NATIONAL;
	}
	@Transient
	public boolean isIntracommunity() {
		return getTransaction() == InvoiceTransactionType.INTRACOMMUNITY;
	}
	@Transient
	public boolean isExtracommunity() {
		return getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY;
	}
	@Transient
	public boolean isCanCeuMel() {
		return getTransaction() == InvoiceTransactionType.CAN_CEU_MEL;
	}
	@Transient
	public boolean isRectifier() {
		return (isNormalRectifier() || isSpecialRectifier());
	}
	@Transient
	public boolean isNormalRectifier() {
		return getRectificationType() == RectificationType.NORMAL_RECTIFIER;
	}
	@Transient
	public boolean isSpecialRectifier() {
		return getRectificationType() == RectificationType.SPECIAL_RECTIFIER;
	}
	@Transient
	public boolean isRectified() {
		return (getRectificationType() == RectificationType.RECTIFIED);
	}
	@Transient
	public List<Invoice> getRectificationInvoices() throws ManagerBeanException {
		if (isRectified()) {
			List<Invoice> rectificationInvoices = new LinkedList<Invoice>();
			if (getRectificationInvoice() != null && getRectificationInvoice().getId() != null) {
				rectificationInvoices.add(getRectificationInvoice());
			} else {
				IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_RECTIFICATION_INVOICE_ID), getId());
				for (ITransferObject ito : invoiceBean.getList(criteria)) {
					Invoice rectifier = (Invoice)ito;
					rectificationInvoices.add(rectifier);
				}
			}
			return rectificationInvoices;
		} else {
			return null;
		}
	}
	@Transient
	public String getRectificationInvoicesString() throws ManagerBeanException {
		String rectificationInvoiceStr = "";
		if (isRectified()) {
			for (Invoice rectifier : getRectificationInvoices()) {
				rectificationInvoiceStr += rectificationInvoiceStr.equals("") ? "" : " - ";
				rectificationInvoiceStr += rectifier.getReferenceCode();
			}
		}
		return rectificationInvoiceStr;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Invoice o = (Invoice) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.comments,o.comments)
			.append(this.investment,o.investment)
			.append(this.issueDate,o.issueDate)
			.append(this.number,o.number)
			.append(this.project,o.project)
			.append(this.rectificationInvoice,o.rectificationInvoice)
			.append(this.rectificationType,o.rectificationType)
			.append(this.referenceCode,o.referenceCode)
			.append(this.registry,o.registry)
			.append(this.registryAddress,o.registryAddress)
			.append(this.registryDocument,o.registryDocument)
			.append(this.registryDocumentCountry,o.registryDocumentCountry)
			.append(this.registryDocumentType,o.registryDocumentType)
			.append(this.registryName,o.registryName)
			.append(this.retentionQuota,o.retentionQuota)
			.append(this.scope,o.scope)
			.append(this.securityLevel,o.securityLevel)
			.append(this.series,o.series)
			.append(this.service,o.service)
			.append(this.signed,o.signed)
			.append(this.status,o.status)
			.append(this.surcharge,o.surcharge)
			.append(this.taxableBase,o.taxableBase)
			.append(this.taxDate,o.taxDate)
			.append(this.taxFree,o.taxFree)
			.append(this.total,o.total)
			.append(this.transaction,o.transaction)
			.append(this.type,o.type)
			.append(this.vatQuota,o.vatQuota)
			.append(this.withholding,o.withholding)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(comments)
			.append(id)
			.append(investment)
			.append(issueDate)
			.append(number)
			.append(project)
			.append(rectificationInvoice)
			.append(rectificationType)
			.append(referenceCode)
			.append(registry)
			.append(registryAddress)
			.append(registryDocument)
			.append(registryDocumentCountry)
			.append(registryDocumentType)
			.append(registryName)
			.append(retentionQuota)
			.append(scope)
			.append(securityLevel)
			.append(series)
			.append(service)
			.append(signed)
			.append(status)
			.append(surcharge)
			.append(taxableBase)
			.append(taxDate)		
			.append(taxFree)		
			.append(total)
			.append(transaction)		
			.append(type)		
			.append(vatQuota)
			.append(withholding)		
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}