package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class RegistryJSON {


	public static Registry fromJSON(JSONObject json) {
		if(json == null) {
			return new Registry();
		}
		
		return new Registry() 
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)))
			.setDocument(json.optString(IJsonNames.DOCUMENT))
			.setDocumentCountry(Country.safeValueOf(json.optString(IJsonNames.DOCUMENT_COUNTRY)))
			.setDocumentType(DocumentType.safeValueOf(json.optString(IJsonNames.DOCUMENT_TYPE)))
			.setName(json.optString(IJsonNames.NAME))
			.setAlias(json.optString(IJsonNames.ALIAS))
			.setLegalPerson(json.optBoolean(IJsonNames.LEGAL_PERSON))
			.setNationality(Country.safeValueOf(json.optString(IJsonNames.NATIONALITY)))
			.setConfidential(json.optBoolean(IJsonNames.CONFIDENTIAL));
	}
	
	public static JSONObject toJSON(Registry registry) {
		return new JSONObject()
			.put(IJsonNames.ID, registry.getId())
			.put(IJsonNames.DOMAIN, DomainJSON.toJSON(registry.getDomain()))
			.put(IJsonNames.DOCUMENT, registry.getDocument())
			.put(IJsonNames.DOCUMENT_COUNTRY, registry.getDocumentCountry() != null ? registry.getDocumentCountry().getIso2(): null)
			.put(IJsonNames.DOCUMENT_TYPE, registry.getDocumentType() != null ? registry.getDocumentType().name(): null)
			.put(IJsonNames.NAME, registry.getName())
			.put(IJsonNames.ALIAS, registry.getAlias())
			.put(IJsonNames.LEGAL_PERSON, registry.isLegalPerson())
			.put(IJsonNames.NATIONALITY, registry.getNationality() != null ? registry.getNationality().getIso2() : null)
			.put(IJsonNames.CONFIDENTIAL, registry.isConfidential());
	}
}
