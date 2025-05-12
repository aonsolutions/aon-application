package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class TargetFull extends RegistryFull<Target> {

	private static final long serialVersionUID = -2437412710996159887L;
	
	public static TargetFull initialize(int domain) {
		TargetFull full = new TargetFull();
		full.setRegistry(new Target());
		full.getRegistry()
		.setDomain(new Domain().setId(domain))
		.setDocumentType(DocumentType.CIF)
		.setDocumentCountry(Country.ES)
		.setNationality(Country.ES);
		full.initializeChilds();
		return full;
	}
	
}
