package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;

public class Seller extends Registry implements Serializable {

	private static final long serialVersionUID = -4021529272657422784L;
	
	private Integer scope;
	private boolean active;

	public Integer getScope() {
		return scope;
	}
	public Seller setScope(Integer scope) {
		this.scope = scope;
		return this;
	}
	
	public boolean isActive() {
		return active;
	}
	public Seller setActive(boolean active) {
		this.active = active;
		return this;
	}

	
	@Override
	public Seller setId(Integer id) {
		super.setId(id);
		return this;
	}
	@Override
	public Seller setDomain(Integer domain) {
		super.setDomain(domain);
		return this;
	}
	@Override
	public Seller setDocument(String document) {
		super.setDocument(document);
		return this;
	}
	@Override
	public Seller setDocumentType(DocumentType documentType) {
		super.setDocumentType(documentType);
		return this;
	}
	@Override
	public Seller setDocumentCountry(Country documentCountry) {
		super.setDocumentCountry(documentCountry);
		return this;
	}
	@Override
	public Seller setName(String name) {
		super.setName(name);
		return this;
	}
	@Override
	public Seller setAlias(String alias) {
		super.setAlias(alias);
		return this;
	}
	@Override
	public Seller setLegalPerson(boolean legalPerson) {
		super.setLegalPerson(legalPerson);
		return this;
	}
	@Override
	public Seller setNationality(Country nationality) {
		super.setNationality(nationality);
		return this;
	}
	@Override
	public Seller setConfidential(boolean confidential) {
		super.setConfidential(confidential);
		return this;
	}
}
