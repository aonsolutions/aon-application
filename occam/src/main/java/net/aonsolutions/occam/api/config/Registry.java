package net.aonsolutions.occam.api.config;

import java.io.Serializable;
import java.util.Objects;

import net.aonsolutions.occam.api.HasDirtyFlag;
import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.watson.client.util.AonNumberUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Registry implements Serializable, HasSelector<Registry>,HasDirtyFlag<Registry>{
	
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
	
	private boolean dirty;
	private boolean selected;
	
	public Integer getId() {
		return id;
	}
	public Registry setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Registry setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain, domain) );
		this.domain = domain;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public Registry setDocument(String document) {
		this.dirtyMark( AonObjectUtils.notEquals(this.document, document) );
		this.document = document;
		return this;
	}

	public DocumentType getDocumentType() {
		return documentType;
	}
	public Registry setDocumentType(DocumentType documentType) {
		this.dirtyMark( AonObjectUtils.notEquals(this.documentType, documentType) );
		this.documentType = documentType;
		return this;
	}
	
	public Country getDocumentCountry() {
		return documentCountry;
	}
	public Registry setDocumentCountry(Country documentCountry) {
		this.dirtyMark( AonObjectUtils.notEquals(this.documentCountry, documentCountry) );
		this.documentCountry = documentCountry;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Registry setName(String name) {
		this.dirtyMark( AonObjectUtils.notEquals(this.name, name) );
		this.name = name;
		return this;
	}
	
	public String getAlias() {
		return alias;
	}
	public Registry setAlias(String alias) {
		this.dirtyMark( AonObjectUtils.notEquals(this.alias, alias) );
		this.alias = alias;
		return this;
	}

	public Country getNationality() {
		return nationality;
	}
	public Registry setNationality(Country nationality) {
		this.dirtyMark( AonObjectUtils.notEquals(this.nationality, nationality) );
		this.nationality = nationality;
		return this;
	}
	
	public boolean isConfidential() {
		return confidential;
	}
	public Registry setConfidential(boolean confidential) {
		this.dirtyMark( AonObjectUtils.notEquals(this.confidential, confidential) );
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}
	@Override
	public Registry setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Registry setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj instanceof Registry other) {
			return AonNumberUtils.equals(this.id,other.id);
		}
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return 31 * 7 + Objects.requireNonNullElse(id, 0).hashCode();
	}
}
