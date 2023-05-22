package net.aonsolutions.occam.api.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import net.aonsolutions.occam.api.HasConfidentiality;
import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.api.config.Audit;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.InvoiceType.InvoiceTypeVisitor;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.client.util.AonStringUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Invoice implements Serializable, HasConfidentiality<Invoice>, HasSelector<Invoice>, HasDirtyFlag<Invoice> {

	private static final long serialVersionUID = 8287791278573182024L;
	
	private Integer id;
	private Integer domain;
	private InvoiceType type;
	private String series;
	private int number;
	private String referenceCode;
	private Date issueDate;
	private Date taxDate;
	private boolean confidential;
	
	private Integer registry;
	private String registryDocument;
	private DocumentType registryDocumentType;
	private Country registryDocumentCountry;
	private String registryName;
	
	private Audit audit;
	
	private Activity activity; 
	
	private boolean dirty;
	private boolean selected;

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
	
	public InvoiceType getType() {
		return type;
	}
	public Invoice setType(InvoiceType type) {
		this.dirtyMark( AonObjectUtils.notEquals(this.type,type) );
		this.type = type;
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
	
	public int getNumber() {
		return number;
	}
	public Invoice setNumber(int number) {
		this.dirtyMark( AonObjectUtils.notEquals(this.number,number) );
		this.number = number;
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
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Invoice setRegistryDocument(String registryDocument) {
		this.dirtyMark( AonObjectUtils.notEquals(this.registryDocument,registryDocument) );
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Invoice setRegistryDocumentType(DocumentType registryDocumentType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.registryDocumentType,registryDocumentType) );
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Invoice setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.dirtyMark( AonObjectUtils.notEquals(this.registryDocumentCountry,registryDocumentCountry) );
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Invoice setRegistryName(String registryName) {
		this.dirtyMark( AonObjectUtils.notEquals(this.registryName,registryName) );
		this.registryName = registryName;
		return this;
	}

	public Optional<Activity> getActivity() {
		return Optional.ofNullable( activity );
	}
	public Invoice setActivity(Activity activity) {
		this.dirtyMark( AonObjectUtils.notEquals(this.activity,activity) );
		this.activity = activity;
		return this;
	}
	
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
	
	// ---------------------------------------------------------- HasSelector<Invoice>
	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Invoice setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	// ---------------------------------------------------------- HasConfidentiality<Invoice>
	@Override
	public boolean isConfidential() {
		return this.confidential;
	}
	@Override
	public Invoice setConfidential(boolean confidential) {
		this.dirtyMark( AonObjectUtils.notEquals(this.confidential,confidential) );
		this.confidential = confidential;
		return this;
	}
	
}

