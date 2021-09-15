package com.esferalia.aon.occam.api.model.registry;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class CompanyFull extends RegistryFull<Company> {

	private static final long serialVersionUID = 2481288189326103898L;
	
	private LinkedList<RDirStaff> dirStaff;
	
	public Company ensureCompany() {
		if (getRegistry() == null) {
			setRegistry(new Company());
		}
		return getRegistry();
	}
	
	public static CompanyFull initialize(int domain) {
		CompanyFull full = new CompanyFull();
		full.setRegistry(new Company());
		full.getRegistry()
			.setDomain(new Domain().setId(domain))
			.setDocumentType(DocumentType.CIF)
			.setDocumentCountry(Country.ES)
			.setNationality(Country.ES);
		full.initializeChilds();
		return full;
	}

	// ------------------------------------------ REGISTRY ADDRESS
	public LinkedList<RDirStaff> getDirStaff() {
		return dirStaff;
	}
	public CompanyFull setDirStaff(LinkedList<RDirStaff> dirStaff) {
		this.dirStaff = dirStaff;
		return this;
	}
	private LinkedList<RDirStaff> ensureDirStaff() {
		if (this.dirStaff == null) this.dirStaff = new LinkedList<>(); 
		return this.dirStaff;
	}
	public CompanyFull addRDirStaff(RDirStaff rDirStaff) {
		ensureDirStaff().add(rDirStaff);
		return this;
	}
	public boolean hasDirStaff() {
		return this.dirStaff != null && !this.dirStaff.isEmpty();
	}
	
}
