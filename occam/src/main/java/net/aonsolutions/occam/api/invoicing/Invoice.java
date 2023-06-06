package net.aonsolutions.occam.api.invoicing;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.occam.api.constants.TransactionType;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.client.util.AonStringUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Invoice extends OccamEntity {
	
	private static final long serialVersionUID = 5304852840915732613L;
	
	private Integer id;
	private Integer domain;
	private Activity activity;
	private String series;
	private Integer number;
	private String referenceCode;
	private Date issueDate;
	private Date taxDate;
	private boolean confidential;
	private Integer registry;
	private String document;
	private DocumentType documentType;
	private Country documentCountry;
	private String name;
	private Scope scope;
	private InvoiceType type;
	private TransactionType transaction;
	private boolean surcharge;
	private boolean withholding;
	private boolean withholdingFarmer;
	private boolean vatAccrualPayment;
	private boolean investment;
	private boolean service;
	private boolean annulled;
	private Double total;
	private Audit audit;
	private LinkedList<InvoiceDetail> details;
	private LinkedList<InvoiceBreakdown> breakdown;

	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Invoice markAsClean() {
		super.markAsClean();
		return this;
	}
		
	public Integer getId() {
		return id;
	}
	public Invoice setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(AonNames.ID));
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Invoice setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(AonNames.DOMAIN));
		this.domain = domain;
		return this;
	}
	
	public Optional<Activity> getActivity() {
		return Optional.ofNullable(activity);
	}
	public Invoice setActivity(Activity activity) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.activity,activity), () -> markAsDirty(AonNames.ACTIVITY));
		this.activity = activity;
		return this;
	}
	
	public String getSeries() {
		return series;
	}
	public Invoice setSeries(String series) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.series,series), () -> markAsDirty(AonNames.SERIES));
		this.series = series;
		return this;
	}
	
	public Integer getNumber() {
		return number;
	}
	public Invoice setNumber(Integer number) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.number,number), () -> markAsDirty(AonNames.NUMBER));
		this.number = number;
		return this;
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public Invoice setReferenceCode(String referenceCode) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.referenceCode,referenceCode), () -> markAsDirty(AonNames.REFERENCE_CODE));
		this.referenceCode = referenceCode;
		return this;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public Invoice setIssueDate(Date issueDate) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.issueDate,issueDate), () -> markAsDirty(AonNames.ISSUE_DATE));
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public Invoice setTaxDate(Date taxDate) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.taxDate,taxDate), () -> markAsDirty(AonNames.TAX_DATE));
		this.taxDate = taxDate;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public Invoice setRegistry(Integer registry) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.registry,registry), () -> markAsDirty(AonNames.REGISTRY));
		this.registry = registry;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public Invoice setDocument(String document) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.document,document), () -> markAsDirty(AonNames.DOCUMENT));
		this.document = document;
		return this;
	}
	
	public DocumentType getDocumentType() {
		return documentType;
	}
	public Invoice setDocumentType(DocumentType documentType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.documentType,documentType), () -> markAsDirty(AonNames.DOCUMENT_TYPE));
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public Invoice setDocumentCountry(Country documentCountry) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.documentCountry,documentCountry), () -> markAsDirty(AonNames.DOCUMENT_COUNTRY));
		this.documentCountry = documentCountry;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Invoice setName(String name) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.name,name), () -> markAsDirty(AonNames.NAME));
		this.name = name;
		return this;
	}

	public Optional<Scope> getScope() {
		return Optional.ofNullable(scope);
	}
	public Invoice setScope(Scope scope) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.scope,scope), () -> markAsDirty(AonNames.SCOPE));
		this.scope = scope;
		return this;
	}
	
	public InvoiceType getType() {
		return type;
	}
	public Invoice setType(InvoiceType type) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.type,type), () -> markAsDirty(AonNames.TYPE));
		this.type = type;
		return this;
	}
	
	public TransactionType getTransaction() {
		return transaction;
	}
	public Invoice setTransaction(TransactionType transaction) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.transaction,transaction), () -> markAsDirty(AonNames.TRANSACTION));
		this.transaction = transaction;
		return this;
	}
	
	public boolean isSurcharge() {
		return surcharge;
	}
	public Invoice setSurcharge(boolean surcharge) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.surcharge,surcharge), () -> markAsDirty(AonNames.SURCHARGE));
		this.surcharge = surcharge;
		return this;
	}
	
	public boolean isWithholding() {
		return withholding;
	}
	public Invoice setWithholding(boolean withholding) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.withholding,withholding), () -> markAsDirty(AonNames.WITHHOLDING));
		this.withholding = withholding;
		return this;
	}
	
	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public Invoice setWithholdingFarmer(boolean withholdingFarmer) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.withholdingFarmer,withholdingFarmer), () -> markAsDirty(AonNames.WITHHOLDING_FARMER));
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}
	
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Invoice setVatAccrualPayment(boolean vatAccrualPayment) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.vatAccrualPayment,vatAccrualPayment), () -> markAsDirty(AonNames.VAT_ACCRUAL_PAYMENT));
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	
	public boolean isInvestment() {
		return investment;
	}
	public Invoice setInvestment(boolean investment) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.investment,investment), () -> markAsDirty(AonNames.INVESTMENT));
		this.investment = investment;
		return this;
	}
	
	public boolean isService() {
		return service;
	}
	public Invoice setService(boolean service) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.service,service), () -> markAsDirty(AonNames.SERVICE));
		this.service = service;
		return this;
	}
	
	public boolean isAnnulled() {
		return annulled;
	}
	public Invoice setAnnulled(boolean annulled) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.annulled,annulled), () -> markAsDirty(AonNames.ANNULLED));
		this.annulled = annulled;
		return this;
	}
	
	public Double getTotal() {
		return total;
	}
	public Invoice setTotal(Double total) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.total,total), () -> markAsDirty(AonNames.TOTAL));
		this.total = total;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public Invoice setConfidential(boolean confidential) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.confidential,confidential), () -> markAsDirty(AonNames.CONFIDENTIAL));
		this.confidential = confidential;
		return this;
	}
	
	public String getDocumentNumber() {
		StringBuilder buf = new StringBuilder();
		Optional.of(getType())
			.ifPresent(t -> t.visit(new InvoiceTypeVisitor<StringBuilder,StringBuilder>() {
				@Override public StringBuilder visitPurchase(StringBuilder t) { return t.append("R"); }
				@Override public StringBuilder visitSales(StringBuilder t) { return t.append("E"); }
				@Override public StringBuilder visitExpenses(StringBuilder t) { return t.append("R"); }
				@Override public StringBuilder visitUndeductible(StringBuilder t) { return t.append("G"); }
			}, buf ));
		buf.append("-");
		if (AonStringUtils.isNotEmpty(getSeries())) {
			buf.append(getSeries()).append("/");
		}
		buf.append(AonStringUtils.leftPad(AonNumberUtils.toString(getNumber()), 6, "0"));
		return buf.toString();
	}
	
	// ---------------------------------------------------------- AUDIT
	public Optional<Audit> getAudit() {
		return Optional.ofNullable(audit);
	}
	public Invoice setAudit(Audit audit) {
		this.audit = audit;
		return this;
	}
	
	public Optional<List<InvoiceDetail>> getDetails() {
		return Optional.ofNullable(details);
	}
	public Invoice setDetails(List<InvoiceDetail> details) {
		this.details = new LinkedList<>();
		this.details.addAll(details);
		return this;
	}
	public Invoice addDetail(InvoiceDetail invoiceDetail) {
		AonObjectUtils.ifTrue(invoiceDetail.isDirty(), () -> markAsDirty(AonNames.DETAILS));
		getDetails().orElse(new LinkedList<>()).add(invoiceDetail);
		return this;
	}

	public Optional<List<InvoiceBreakdown>> getBreakdown() {
		return Optional.ofNullable(breakdown);
	}
	public Invoice setBreakdown(List<InvoiceBreakdown> breakdown) {
		this.breakdown = new LinkedList<>();
		this.breakdown.addAll(breakdown);
		return this;
	}
	public Invoice addBreakdown(InvoiceBreakdown invoiceBreakdown) {
		AonObjectUtils.ifTrue(invoiceBreakdown.isDirty(), () -> markAsDirty(AonNames.BREAKDOWN));
		getBreakdown().orElse(new LinkedList<>()).add(invoiceBreakdown);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Invoice other) {
			return AonObjectUtils.equals(this.getUuid(),other.getUuid());
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}

