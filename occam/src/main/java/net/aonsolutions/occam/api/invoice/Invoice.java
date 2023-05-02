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
import net.aonsolutions.occam.api.config.Activity;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.InvoiceType.InvoiceTypeVisitor;

public class Invoice implements Serializable, HasAudit<Invoice>, HasConfidentiality<Invoice>, HasSelector<Invoice> {

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
	
	private Activity activity; 
	
	private boolean dirty;
	private boolean selected;

	public Integer getId() {
		return id;
	}
	public Invoice setId(Integer id) {
		this.dirtyMark( AonUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Invoice setDomain(Integer domain) {
		this.dirtyMark( AonUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}
	
	public InvoiceType getType() {
		return type;
	}
	public Invoice setType(InvoiceType type) {
		this.dirtyMark( AonUtils.notEquals(this.type,type) );
		this.type = type;
		return this;
	}
	
	public String getSeries() {
		return series;
	}
	public Invoice setSeries(String series) {
		this.dirtyMark( AonUtils.notEquals(this.series,series) );
		this.series = series;
		return this;
	}
	
	public int getNumber() {
		return number;
	}
	public Invoice setNumber(int number) {
		this.dirtyMark( AonUtils.notEquals(this.number,number) );
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
	public Invoice setReferenceCode(String referenceCode) {
		this.dirtyMark( AonUtils.notEquals(this.referenceCode,referenceCode) );
		this.referenceCode = referenceCode;
		return this;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public Invoice setIssueDate(Date issueDate) {
		this.dirtyMark( AonUtils.notEquals(this.issueDate,issueDate) );
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public Invoice setTaxDate(Date taxDate) {
		this.dirtyMark( AonUtils.notEquals(this.taxDate,taxDate) );
		this.taxDate = taxDate;
		return this;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public Invoice setRegistry(Integer registry) {
		this.dirtyMark( AonUtils.notEquals(this.registry,registry) );
		this.registry = registry;
		return this;
	}
	
	public String getRegistryDocument() {
		return registryDocument;
	}
	public Invoice setRegistryDocument(String registryDocument) {
		this.dirtyMark( AonUtils.notEquals(this.registryDocument,registryDocument) );
		this.registryDocument = registryDocument;
		return this;
	}
	public DocumentType getRegistryDocumentType() {
		return registryDocumentType;
	}
	public Invoice setRegistryDocumentType(DocumentType registryDocumentType) {
		this.dirtyMark( AonUtils.notEquals(this.registryDocumentType,registryDocumentType) );
		this.registryDocumentType = registryDocumentType;
		return this;
	}
	public Country getRegistryDocumentCountry() {
		return registryDocumentCountry;
	}
	public Invoice setRegistryDocumentCountry(Country registryDocumentCountry) {
		this.dirtyMark( AonUtils.notEquals(this.registryDocumentCountry,registryDocumentCountry) );
		this.registryDocumentCountry = registryDocumentCountry;
		return this;
	}
	public String getRegistryName() {
		return registryName;
	}
	public Invoice setRegistryName(String registryName) {
		this.dirtyMark( AonUtils.notEquals(this.registryName,registryName) );
		this.registryName = registryName;
		return this;
	}

	public Optional<Activity> getActivity() {
		return Optional.ofNullable( activity );
	}
	public Invoice setActivity(Activity activity) {
		this.dirtyMark( AonUtils.notEquals(this.activity,activity) );
		this.activity = activity;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	public Invoice setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	public Invoice dirtyMark(boolean dirty) {
		this.dirty = isDirty() || dirty;
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
		this.dirtyMark( AonUtils.notEquals(this.confidential,confidential) );
		this.confidential = confidential;
		return this;
	}
	// ---------------------------------------------------------- HasAudit<Invoice>
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	@Override
	public Invoice setCreationUser(String creationUser) {
		this.dirtyMark( AonUtils.notEquals(this.creationUser,creationUser) );
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	@Override
	public Invoice setCreationDate(Date creationDate) {
		this.dirtyMark( AonUtils.notEquals(this.creationDate,creationDate) );
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	@Override
	public Invoice setModificationUser(String modificationUser) {
		this.dirtyMark( AonUtils.notEquals(this.modificationUser,modificationUser) );
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	@Override
	public Invoice setModificationDate(Date modificationDate) {
		this.dirtyMark( AonUtils.notEquals(this.modificationDate,modificationDate) );
		this.modificationDate = modificationDate;
		return this;
	}
	
}

