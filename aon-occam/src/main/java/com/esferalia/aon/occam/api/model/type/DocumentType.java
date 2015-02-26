package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum DocumentType implements Serializable {
	
	NIF("NIF")
	,CIF("CIF")
	,NIE("NIE")
	,PASSPORT("Pasp.")
	,WORK_PERMIT("P.T.")
	,COMMUNITY_CARD("T.C.")
	,OTHER("Otr.")
	;

	private String description;
	
	private DocumentType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
}
