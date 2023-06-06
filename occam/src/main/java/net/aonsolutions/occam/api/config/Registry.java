package net.aonsolutions.occam.api.config;

import java.util.Objects;

import net.aonsolutions.occam.api.OccamEntity;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.metadata.RegistryMetadata;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Registry extends OccamEntity<RegistryMetadata>  {
	
	private static final long serialVersionUID = 9114564405091033572L;
	
	private Integer id;
	private Integer domain;
	private String document;
	private DocumentType documentType;
	private Country documentCountry;
	private String name;
	private String alias;
	private Country nationality;
	private boolean confidential;
	
	@Override
	protected Object getUuid() {
		return getId();
	}
	@Override
	public Registry markAsClean() {
		super.markAsClean();
		return this;
	}

	public Integer getId() {
		return id;
	}
	public Registry setId(Integer id) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.id,id), () -> markAsDirty(RegistryMetadata.ID));
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Registry setDomain(Integer domain) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.domain,domain), () -> markAsDirty(RegistryMetadata.DOMAIN));
		this.domain = domain;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public Registry setDocument(String document) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.document,document), () -> markAsDirty(RegistryMetadata.DOCUMENT));
		this.document = document;
		return this;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}
	public Registry setDocumentType(DocumentType documentType) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.documentType,documentType), () -> markAsDirty(RegistryMetadata.DOCUMENT_TYPE));
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public Registry setDocumentCountry(Country documentCountry) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.documentCountry,documentCountry), () -> markAsDirty(RegistryMetadata.DOCUMENT_COUNTRY));
		this.documentCountry = documentCountry;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Registry setName(String name) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.name,name), () -> markAsDirty(RegistryMetadata.NAME));
		this.name = name;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}
	public Registry setAlias(String alias) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.alias,alias), () -> markAsDirty(RegistryMetadata.ALIAS));
		this.alias = alias;
		return this;
	}

	public Country getNationality() {
		return nationality;
	}
	public Registry setNationality(Country nationality) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.nationality,nationality), () -> markAsDirty(RegistryMetadata.NATIONALITY));
		this.nationality = nationality;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public Registry setConfidential(boolean confidential) {
		AonObjectUtils.ifTrue(AonObjectUtils.notEquals(this.confidential,confidential), () -> markAsDirty(RegistryMetadata.CONFIDENTIAL));
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Registry other) {
			return AonObjectUtils.equals( this.getUuid(),other.getUuid() );
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(getUuid(), 0).hashCode();
	}
}
