package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class Registry implements Serializable{
	
	private static final long serialVersionUID = 9114564405091033572L;
	
	private Integer id;
	private Domain domain;
	private String document;
	private DocumentType documentType;
	private Country documentCountry;
	private String name;
	private String alias;
	private boolean legalPerson;
	private Country nationality;
	private SecurityLevel securityLevel;
	
	private boolean dirty;
	private boolean selected;
	
	/**
	 * @deprecated Use RegistryFull to access registry data.
	 */
	@Deprecated()
	private RAddress mainAddress;
	
	public Integer getId() {
		return id;
	}
	public Registry setId(Integer id) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.id , id) );
		this.id = id;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	public Registry setDomain(Domain domain) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.domain, domain) );
		this.domain = domain;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public Registry setDocument(String document) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.document, document) );
		this.document = document;
		return this;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}
	public Registry setDocumentType(DocumentType documentType) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.documentType, documentType) );
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public Registry setDocumentCountry(Country documentCountry) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.documentCountry, documentCountry) );
		this.documentCountry = documentCountry;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Registry setName(String name) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.name, name) );
		this.name = name;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}
	public Registry setAlias(String alias) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.alias, alias) );
		this.alias = alias;
		return this;
	}

	public boolean isLegalPerson() {
		return legalPerson;
	}
	public Registry setLegalPerson(boolean legalPerson) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.legalPerson, legalPerson) );
		this.legalPerson = legalPerson;
		return this;
	}

	public Country getNationality() {
		return nationality;
	}
	public Registry setNationality(Country nationality) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.nationality, nationality) );
		this.nationality = nationality;
		return this;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Registry setSecurityLevel(SecurityLevel securityLevel) {
		this.setDirty( isDirty()?true:AonUtils.notEquals(this.securityLevel, securityLevel) );
		this.securityLevel = securityLevel;
		return this;
	}
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	public Registry setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}
	public Registry setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isSelected() {
		return selected;
	}
	public Registry setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	/**
	 * @deprecated Use RegistryFull to access registry data.
	 */
	@Deprecated() 
	public RAddress getMainAddress() {
		return mainAddress;
	}
	/**
	 * @deprecated Use RegistryFull to access registry data.
	 */
	@Deprecated()
	public Registry setMainAddress(RAddress mainAddress) {
		this.mainAddress = mainAddress;
		return this;
	}
	
	public static String getFullDescription(Registry registry) {
		return AonStringUtils.defaultIfEmpty(registry.getDocumentType().getDescription(), AonStringUtils.repeat(AonStringUtils.QUESTION, 3))
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.defaultIfEmpty(registry.getDocumentCountry().getIso2(), AonStringUtils.repeat(AonStringUtils.QUESTION, 2)) 
				+ AonStringUtils.SLASH
				+ AonStringUtils.defaultIfEmpty(registry.getDocument(), AonStringUtils.repeat(AonStringUtils.QUESTION, 9))
				+ AonStringUtils.SPACE
				+ AonStringUtils.HYPHEN
				+ AonStringUtils.SPACE
				+ registry.getName()
				+ AonStringUtils.SPACE
				+ (AonStringUtils.isNotBlank(registry.getAlias())
					?(AonStringUtils.SPACE + AonStringUtils.OPEN_PARENTHESIS + registry.getAlias() + AonStringUtils.CLOSE_PARENTHESIS)
					:AonStringUtils.EMPTY)
				;
	}
		
	public Registry get() {
		return new Registry()
			.setId(getId())
			.setDomain(getDomain())
			.setDocument(getDocument())
			.setDocumentType(getDocumentType())
			.setDocumentCountry(getDocumentCountry())
			.setName(getName())
			.setAlias(getAlias())
			.setLegalPerson(isLegalPerson())
			.setNationality(getNationality())
			.setSecurityLevel(getSecurityLevel());
	}

	public <T extends Registry> T copy(Registry registry, T child) {
		if (registry != null) {
			child.setId(registry.getId());
			child.setDomain(registry.getDomain());
			child.setDocument(registry.getDocument());
			child.setDocumentType(registry.getDocumentType());
			child.setDocumentCountry(registry.getDocumentCountry());
			child.setName(registry.getName());
			child.setAlias(registry.getAlias());
			child.setLegalPerson(registry.isLegalPerson());
			child.setNationality(registry.getNationality());
			child.setSecurityLevel(registry.getSecurityLevel());
		}
		return child;
	}
}
