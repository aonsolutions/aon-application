package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;

import net.aonsolutions.occam.api.model.metadata.InvoiceHeaderMetadata;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.RectificationType;
import net.aonsolutions.occam.api.model.util.InvoiceUtil;

public class InvoiceHeader extends AonEntity<InvoiceHeaderMetadata> implements HasAudit {
	
	private static final long serialVersionUID = 8998986993895056017L;
	
	private Integer id;
	private Integer domain;
	private Activity activity;
	private Integer project;
	private InvoiceType type;
	private String series;
	private Integer number;
	private String referenceCode;
	private InvoiceTransactionType transaction;
	private Date issueDate;
	private Date taxDate;
	
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	private Account registryAccount;
	
	private RectificationType rectificationType;
	private Integer rectificationInvoiceId;
	
	private Integer scope;
	private boolean confidential;
	private boolean recorded;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private boolean investment;
	private boolean service;
	private boolean signed;
	private boolean annulled;
	private double taxableBase;
	private double vatQuota;
	private double retentionQuota;
	private double total;
	private Seller seller;
	
	private String comments;
	private String remarks;

	private String creationUser;
	private Timestamp creationDate;
	private String modificationUser;
	private Timestamp modificationDate;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public InvoiceHeader markAsClean() {
		super.markAsClean();
		return this; 
	}
	
	@Override
	public InvoiceHeader setSelected( boolean selected) {
		super.setSelected(selected);
		return this; 
	}
	
	@Override
	public InvoiceHeader setDeleted( boolean selected) {
		super.setDeleted(selected);
		return this; 
	}

	public Integer getId() {
		return id;
	}
	public InvoiceHeader setId(Integer id) {
		checkIfDirty( this.id,id, InvoiceHeaderMetadata.ID);
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceHeader setDomain(Integer domain) {
		checkIfDirty( this.domain,domain, InvoiceHeaderMetadata.DOMAIN);
		this.domain = domain;
		return this;
	}
	
	public Optional<Activity> getActivity() {
		return Optional.ofNullable(activity);
	}
	public InvoiceHeader setActivity(Activity activity) {
		checkIfDirty( this.activity,activity, InvoiceHeaderMetadata.ACTIVITY);
		this.activity = activity;
		return this;
	}
	
	public Integer getProject() {
		return project;
	}
	public InvoiceHeader setProject(Integer project) {
		checkIfDirty( this.project,project, InvoiceHeaderMetadata.PROJECT);
		this.project = project;
		return this;
	}
	
	public InvoiceType getType() {
		return type;
	}
	public InvoiceHeader setType(InvoiceType type) {
		checkIfDirty( this.type,type, InvoiceHeaderMetadata.TYPE);
		this.type = type;
		return this;
	}

	public String getSeries() {
		return series;
	}
	public InvoiceHeader setSeries(String series) {
		checkIfDirty( this.series,series, InvoiceHeaderMetadata.SERIES);
		this.series = series;
		return this;
	}
	
	public Integer getNumber() {
		return number;
	}
	public InvoiceHeader setNumber(Integer number) {
		checkIfDirty( this.number,number, InvoiceHeaderMetadata.NUMBER);
		this.number = number;
		return this;
	}
	
	public String getDocumentNumber() {
		return InvoiceUtil.getDocumentNumber(type, series, number);
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public InvoiceHeader setReferenceCode(String referenceCode) {
		checkIfDirty( this.referenceCode,referenceCode, InvoiceHeaderMetadata.REFERENCE_CODE);
		this.referenceCode = referenceCode;
		return this;
	}
	
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public InvoiceHeader setTransaction(InvoiceTransactionType transaction) {
		checkIfDirty( this.transaction,transaction, InvoiceHeaderMetadata.TRANSACTION);
		this.transaction = transaction;
		return this;
	}

	public Date getIssueDate() {
		return issueDate;
	}
	public InvoiceHeader setIssueDate(Date issueDate) {
		checkIfDirty( this.issueDate,issueDate, InvoiceHeaderMetadata.ISSUE_DATE);
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public InvoiceHeader setTaxDate(Date taxDate) {
		checkIfDirty( this.taxDate,taxDate, InvoiceHeaderMetadata.TAX_DATE);
		this.taxDate = taxDate;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public InvoiceHeader setRegistry(Integer registry) {
		checkIfDirty( this.registry,registry, InvoiceHeaderMetadata.REGISTRY);
		this.registry = registry;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public InvoiceHeader setRegistryDocument(String registryDocument) {
		checkIfDirty( this.registryDocument,registryDocument, InvoiceHeaderMetadata.RDOCUMENT);
		this.registryDocument = registryDocument;
		return this;
	}
	
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public InvoiceHeader setRegistryDocumentType(DocumentType registryDocumentType) {
		checkIfDirty( this.registryDocumentType,registryDocumentType, InvoiceHeaderMetadata.RDOCUMENT_TYPE);
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public InvoiceHeader setRegistryDocumentCountry(Country registryDocumentCountry) {
		checkIfDirty( this.registryDocumentCountry,registryDocumentCountry, InvoiceHeaderMetadata.RDOCUMENT_COUNTRY);
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	
	public String getRegistryName() {
		return registryName;
	}
	public InvoiceHeader setRegistryName(String registryName) {
		checkIfDirty( this.registryName,registryName, InvoiceHeaderMetadata.RNAME);
		this.registryName = registryName;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	public InvoiceHeader setConfidential(boolean confidential) {
		checkIfDirty( this.confidential,confidential, InvoiceHeaderMetadata.SECURITY_LEVEL);
		this.confidential = confidential;
		return this;
	}

	public boolean isRecorded() {
		return recorded;
	}
	public InvoiceHeader setRecorded(boolean recorded) {
		checkIfDirty( this.recorded,recorded, InvoiceHeaderMetadata.STATUS);
		this.recorded = recorded;
		return this;
	}
	
	public RectificationType getRectificationType() {
		return rectificationType;
	}
	public InvoiceHeader setRectificationType(RectificationType rectificationType) {
		checkIfDirty( this.rectificationType,rectificationType, InvoiceHeaderMetadata.RECTIFICATION_TYPE);
		this.rectificationType = rectificationType;
		return this;
	}
	public boolean isRectified() {
		return (getRectificationType() == RectificationType.RECTIFIED);
	}
	public boolean isRectifier() {
		return (getRectificationType() == RectificationType.NORMAL_RECTIFIER 
			|| getRectificationType() == RectificationType.SPECIAL_RECTIFIER);
	}
	public Integer getRectificationInvoiceId() {
		return rectificationInvoiceId;
	}
	public InvoiceHeader setRectificationInvoiceId(Integer rectificationInvoiceId) {
		checkIfDirty( this.rectificationInvoiceId,rectificationInvoiceId, InvoiceHeaderMetadata.RECTIFICATION_INVOICE);
		this.rectificationInvoiceId = rectificationInvoiceId;
		return this;
	}
	
	public Optional<Account> getRegistryAccount() {
		return Optional.ofNullable(registryAccount);
	}
	public InvoiceHeader setRegistryAccount(Account registryAccount) {
		checkIfDirty( this.rectificationInvoiceId,rectificationInvoiceId, InvoiceHeaderMetadata.ACCOUNT);
		this.registryAccount = registryAccount;
		return this;
	}
	
	public Integer getScope() {
		return scope;
	}
	public InvoiceHeader setScope(Integer scope) {
		checkIfDirty( this.scope,scope, InvoiceHeaderMetadata.SCOPE);
		this.scope = scope;
		return this;
	}
	
	public boolean isSurcharge() {
		return surcharge;
	}
	public InvoiceHeader setSurcharge(boolean surcharge) {
		checkIfDirty( this.surcharge,surcharge, InvoiceHeaderMetadata.SURCHARGE);
		this.surcharge = surcharge;
		return this;
	}
	
	public boolean isWithholding() {
		return withholding;
	}
	public InvoiceHeader setWithholding(boolean withholding) {
		checkIfDirty( this.withholding,withholding, InvoiceHeaderMetadata.WITHHOLDING);
		this.withholding = withholding;
		return this;
	}
	
	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public InvoiceHeader setWithholdingFarmer(boolean withholdingFarmer) {
		checkIfDirty( this.withholdingFarmer,withholdingFarmer, InvoiceHeaderMetadata.WITHHOLDING_FARMER );
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}
	
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public InvoiceHeader setVatAccrualPayment(boolean vatAccrualPayment) {
		checkIfDirty( this.vatAccrualPayment,vatAccrualPayment, InvoiceHeaderMetadata.VAT_ACCRUAL_PAYMENT );
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	
	public boolean isInvestment() {
		return investment;
	}
	public InvoiceHeader setInvestment(boolean investment) {
		checkIfDirty( this.investment,investment, InvoiceHeaderMetadata.INVESTMENT );
		this.investment = investment;
		return this;
	}
	
	public boolean isService() {
		return service;
	}
	public InvoiceHeader setService(boolean service) {
		checkIfDirty( this.service,service, InvoiceHeaderMetadata.SERVICE );
		this.service = service;
		return this;
	}
	
	public boolean isSigned() {
		return signed;
	}
	public InvoiceHeader setSigned(boolean signed) {
		checkIfDirty( this.signed,signed, InvoiceHeaderMetadata.SIGNED);
		this.signed = signed;
		return this;
	}
	
	public boolean isAnnulled() {
		return annulled;
	}
	public InvoiceHeader setAnnulled(boolean annulled) {
		this.annulled = annulled;
		return this;
	}
	
	public double getTaxableBase() {
		return taxableBase;
	}
	public InvoiceHeader setTaxableBase(double taxableBase) {
		checkIfDirty( this.taxableBase,taxableBase, InvoiceHeaderMetadata.TAXABLE_BASE);
		this.taxableBase = taxableBase;
		return this;
	}
	
	public double getVatQuota() {
		return vatQuota;
	}
	public InvoiceHeader setVatQuota(double vatQuota) {
		checkIfDirty( this.vatQuota,vatQuota, InvoiceHeaderMetadata.VAT_QUOTA);
		this.vatQuota = vatQuota;
		return this;
	}
	
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public InvoiceHeader setRetentionQuota(double retentionQuota) {
		checkIfDirty( this.retentionQuota,retentionQuota, InvoiceHeaderMetadata.RETENTION_QUOTA);
		this.retentionQuota = retentionQuota;
		return this;
	}
	
	public double getTotal() {
		return total;
	}
	public InvoiceHeader setTotal(double total) {
		checkIfDirty( this.total,total, InvoiceHeaderMetadata.TOTAL);
		this.total = total;
		return this;
	}
	
	public double getOtherAmount() {
		return AonMathUtils.round( getTotal() - getTaxableBase() - getVatQuota() + getRetentionQuota() );
	}
	
	public Optional<Seller> getSeller() {
		return Optional.ofNullable(seller);
	}
	public InvoiceHeader setSeller(Seller seller) {
		checkIfDirty( this.seller,seller, InvoiceHeaderMetadata.SELLER);
		this.seller = seller;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	public InvoiceHeader setComments(String comments) {
		checkIfDirty( this.comments,comments, InvoiceHeaderMetadata.COMMENTS);
		this.comments = comments;
		return this;
	}

	public String getRemarks() {
		return remarks;
	}
	public InvoiceHeader setRemarks(String remarks) {
		checkIfDirty( this.remarks,remarks, InvoiceHeaderMetadata.REMARKS);
		this.remarks = remarks;
		return this;
	}
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public InvoiceHeader setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Timestamp getCreationDate() {
		return creationDate;
	}
	public InvoiceHeader setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public InvoiceHeader setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Timestamp getModificationDate() {
		return modificationDate;
	}
	public InvoiceHeader setModificationDate(Timestamp modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	// ---------------------------------------------------------- UTIL
	public boolean isNational() {
		return getTransaction() == InvoiceTransactionType.NATIONAL;
	}
	public boolean isNotNational() {
		return isIntracommunity() || isExtracommunity() || isCanCeuMel();
	}
	public boolean isIntracommunity() {
		return getTransaction() == InvoiceTransactionType.INTRACOMMUNITY;
	}
	public boolean isExtracommunity() {
		return getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY;
	}
	public boolean isDUAAllowed() {
		return isNational() 
			&& !isUndeductible() 
			&& (isPurchase() || isExpenses());
	}
	public boolean isDUALinkAllowed() {
		return !isUndeductible() 
			&& (isPurchase() || isExpenses()) 
			&& (isExtracommunity() || isCanCeuMel()) ;
	}
	public boolean isCanCeuMel() {
		return getTransaction() == InvoiceTransactionType.CAN_CEU_MEL;
	}
	public boolean isIsp() {
		return getTransaction() == InvoiceTransactionType.OTHER_ISP;
	}
	public boolean isSales() {
		return getType() == InvoiceType.SALES;
	}
	public boolean isNotSales() {
		return !isSales();
	}
	public boolean isPurchase() {
		return getType() == InvoiceType.PURCHASE;
	}
	public boolean isExpenses() {
		return getType() == InvoiceType.EXPENSES;
	}
	public boolean isUndeductible() {
		return getType() == InvoiceType.UNDEDUCTIBLE;
	}
	
	public boolean mustApplyISP() {
		return (isPurchase() && isIntracommunity())					// Compra intracomunitaria
			|| (isExpenses() && isIntracommunity())					// Gasto intracomunitario
			|| (isPurchase() && isIsp())							// Compra Inversion Sujeto Pasivo
			|| (isExpenses() && isIsp())							// Gasto Inversion Sujeto Pasivo
			|| (isPurchase() && isExtracommunity() && isService())	// Compra extracomunitaria de servicio
			|| (isPurchase() && isCanCeuMel() && isService())		// Compra Canarias de servicio
			|| (isExpenses() && isExtracommunity())					// Gasto extracomunitario
			|| (isExpenses() && isCanCeuMel());						// Gasto Canarias
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof InvoiceHeader other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
	
}

