package net.aonsolutions.occam.api.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

import net.aonsolutions.occam.api.HasAudit;
import net.aonsolutions.occam.api.HasConfidentiality;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.InvoiceType.InvoiceTypeVisitor;

public class InvoiceMin implements Serializable, HasAudit<InvoiceMin>, HasConfidentiality<InvoiceMin>, HasSelector<InvoiceMin> {

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
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private Integer activityId;
	private String activityDescription;
	private String activityEpigraph;
	
	private boolean dirty;
	private boolean selected;

	public Integer getId() {
		return id;
	}
	public InvoiceMin setId(Integer id) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public InvoiceMin setDomain(Integer domain) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}
	
	public InvoiceType getType() {
		return type;
	}
	public InvoiceMin setType(InvoiceType type) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.type,type) );
		this.type = type;
		return this;
	}
	
	public String getSeries() {
		return series;
	}
	public InvoiceMin setSeries(String series) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.series,series) );
		this.series = series;
		return this;
	}
	
	public int getNumber() {
		return number;
	}
	public InvoiceMin setNumber(int number) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.number,number) );
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
		Optional.of(getSeries()).ifPresent(s -> buf.append(series).append("/"));
		buf.append(AonStringUtils.leftPad(AonNumberUtils.toString(number), 6, "0"));
		return buf.toString();
	}
	
	public String getReferenceCode() {
		return referenceCode;
	}
	public InvoiceMin setReferenceCode(String referenceCode) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.referenceCode,referenceCode) );
		this.referenceCode = referenceCode;
		return this;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public InvoiceMin setIssueDate(Date issueDate) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.issueDate,issueDate) );
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public InvoiceMin setTaxDate(Date taxDate) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.taxDate,taxDate) );
		this.taxDate = taxDate;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public InvoiceMin setRegistry(Integer registry) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.registry,registry) );
		this.registry = registry;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public InvoiceMin setRegistryDocument(String registryDocument) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.registryDocument,registryDocument) );
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public InvoiceMin setRegistryDocumentType(DocumentType registryDocumentType) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.registryDocumentType,registryDocumentType) );
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public InvoiceMin setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.registryDocumentCountry,registryDocumentCountry) );
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public InvoiceMin setRegistryName(String registryName) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.registryName,registryName) );
		this.registryName = registryName;
		return this;
	}

	public Integer getActivityId() {
		return activityId;
	}
	public InvoiceMin setActivityId(Integer activityId) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.activityId,activityId) );
		this.activityId = activityId;
		return this;
	}
	
	public String getActivityDescription() {
		return activityDescription;
	}
	public InvoiceMin setActivityDescription(String activityDescription) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.activityDescription,activityDescription) );
		this.activityDescription = activityDescription;
		return this;
	}
	
	public String getActivityEpigraph() {
		return activityEpigraph;
	}
	public InvoiceMin setActivityEpigraph(String activityEpigraph) {
		this.activityEpigraph = activityEpigraph;
		this.setDirty( isDirty() || AonUtils.notEquals(this.activityEpigraph,activityEpigraph) );
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	public InvoiceMin setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	// ---------------------------------------------------------- HasSelector<Invoice>
	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public InvoiceMin setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	// ---------------------------------------------------------- HasConfidentiality<Invoice>
	@Override
	public boolean isConfidential() {
		return this.confidential;
	}
	@Override
	public InvoiceMin setConfidential(boolean confidential) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.confidential,confidential) );
		this.confidential = confidential;
		return this;
	}
	// ---------------------------------------------------------- HasAudit<Invoice>
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	@Override
	public InvoiceMin setCreationUser(String creationUser) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.creationUser,creationUser) );
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	@Override
	public InvoiceMin setCreationDate(Date creationDate) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.creationDate,creationDate) );
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	@Override
	public InvoiceMin setModificationUser(String modificationUser) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.modificationUser,modificationUser) );
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	@Override
	public InvoiceMin setModificationDate(Date modificationDate) {
		this.setDirty( isDirty() || AonUtils.notEquals(this.modificationDate,modificationDate) );
		this.modificationDate = modificationDate;
		return this;
	}
	
}

