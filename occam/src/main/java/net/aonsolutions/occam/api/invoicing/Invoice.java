package net.aonsolutions.occam.api.invoicing;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.aonsolutions.occam.api.HasDirtyFlag;
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

public class Invoice implements Serializable,HasDirtyFlag<Invoice> {
	
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

	private boolean dirty;
		
	public Integer getId() {
		return id;
	}
	public Invoice setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Invoice setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}
	
	public Optional<Activity> getActivity() {
		return Optional.ofNullable(activity);
	}
	public Invoice setActivity(Activity activity) {
		this.dirtyMark( AonObjectUtils.notEquals(this.activity,activity) );
		this.activity = activity;
		return this;
	}
	
	public String getSeries() {
		return series;
	}
	public Invoice setSeries(String series) {
		this.dirtyMark( AonObjectUtils.notEquals(this.series,series) );
		this.series = series;
		return this;
	}
	
	public Integer getNumber() {
		return number;
	}
	public Invoice setNumber(Integer number) {
		this.dirtyMark( AonObjectUtils.notEquals(this.number,number) );
		this.number = number;
		return this;
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public Invoice setReferenceCode(String referenceCode) {
		this.dirtyMark( AonObjectUtils.notEquals(this.referenceCode,referenceCode) );
		this.referenceCode = referenceCode;
		return this;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public Invoice setIssueDate(Date issueDate) {
		this.dirtyMark( AonObjectUtils.notEquals(this.issueDate,issueDate) );
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public Invoice setTaxDate(Date taxDate) {
		this.dirtyMark( AonObjectUtils.notEquals(this.taxDate,taxDate) );
		this.taxDate = taxDate;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public Invoice setRegistry(Integer registry) {
		this.dirtyMark( AonObjectUtils.notEquals(this.registry,registry) );
		this.registry = registry;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public Invoice setDocument(String document) {
		this.dirtyMark( AonObjectUtils.notEquals(this.document,document) );
		this.document = document;
		return this;
	}
	
	public DocumentType getDocumentType() {
		return documentType;
	}
	public Invoice setDocumentType(DocumentType documentType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.documentType,documentType) );
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public Invoice setDocumentCountry(Country documentCountry) {
		this.dirtyMark( AonObjectUtils.notEquals(this.documentCountry,documentCountry) );
		this.documentCountry = documentCountry;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Invoice setName(String name) {
		this.dirtyMark( AonObjectUtils.notEquals(this.name,name) );
		this.name = name;
		return this;
	}

	public Optional<Scope> getScope() {
		return Optional.ofNullable(scope);
	}
	public Invoice setScope(Scope scope) {
		this.dirtyMark( AonObjectUtils.notEquals(this.scope,scope) );
		this.scope = scope;
		return this;
	}
	
	public InvoiceType getType() {
		return type;
	}
	public Invoice setType(InvoiceType type) {
		this.dirtyMark( AonObjectUtils.notEquals(this.type,type) );
		this.type = type;
		return this;
	}
	
	public TransactionType getTransaction() {
		return transaction;
	}
	public Invoice setTransaction(TransactionType transaction) {
		this.dirtyMark( AonObjectUtils.notEquals(this.transaction,transaction) );
		this.transaction = transaction;
		return this;
	}
	
	public boolean isSurcharge() {
		return surcharge;
	}
	public Invoice setSurcharge(boolean surcharge) {
		this.dirtyMark( AonObjectUtils.notEquals(this.surcharge,surcharge) );
		this.surcharge = surcharge;
		return this;
	}
	
	public boolean isWithholding() {
		return withholding;
	}
	public Invoice setWithholding(boolean withholding) {
		this.dirtyMark( AonObjectUtils.notEquals(this.withholding,withholding) );
		this.withholding = withholding;
		return this;
	}
	
	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}
	public Invoice setWithholdingFarmer(boolean withholdingFarmer) {
		this.dirtyMark( AonObjectUtils.notEquals(this.withholdingFarmer,withholdingFarmer) );
		this.withholdingFarmer = withholdingFarmer;
		return this;
	}
	
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public Invoice setVatAccrualPayment(boolean vatAccrualPayment) {
		this.dirtyMark( AonObjectUtils.notEquals(this.vatAccrualPayment,vatAccrualPayment) );
		this.vatAccrualPayment = vatAccrualPayment;
		return this;
	}
	
	public boolean isInvestment() {
		return investment;
	}
	public Invoice setInvestment(boolean investment) {
		this.dirtyMark( AonObjectUtils.notEquals(this.investment,investment) );
		this.investment = investment;
		return this;
	}
	
	public boolean isService() {
		return service;
	}
	public Invoice setService(boolean service) {
		this.dirtyMark( AonObjectUtils.notEquals(this.service,service) );
		this.service = service;
		return this;
	}
	
	public boolean isAnnulled() {
		return annulled;
	}
	public Invoice setAnnulled(boolean annulled) {
		this.dirtyMark( AonObjectUtils.notEquals(this.annulled,annulled) );
		this.annulled = annulled;
		return this;
	}
	
	public Double getTotal() {
		return total;
	}
	public Invoice setTotal(Double total) {
		this.dirtyMark( AonObjectUtils.notEquals(this.total,total) );
		this.total = total;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public Invoice setConfidential(boolean confidential) {
		this.dirtyMark( AonObjectUtils.notEquals(this.confidential,confidential) );
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
	
	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public Invoice setDirty(boolean dirty) {
		this.dirty = dirty;
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
		this.dirtyMark( invoiceDetail.isDirty() );
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
		this.dirtyMark( invoiceBreakdown.isDirty() );
		getBreakdown().orElse(new LinkedList<>()).add(invoiceBreakdown);
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Invoice other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}

