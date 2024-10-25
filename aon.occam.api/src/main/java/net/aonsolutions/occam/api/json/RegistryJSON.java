package net.aonsolutions.occam.api.json;

import java.util.function.Supplier;

import org.json.JSONObject;

import net.aonsolutions.occam.api.model.Registry;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;

public class RegistryJSON {
	
	private RegistryJSON() {
		
	}
	
	public static Registry fromJSON(JSONObject json) {
		return fromJSON(json, Registry::new );
	}
	
	public static <R extends Registry> R fromJSON(JSONObject json, Supplier<R> supplier) {
		if (JsonUtils.isEmpty(json)) return null;
		R registry = supplier.get(); 
		registry.setId(JsonUtils.getInteger(json, IJsonNames.ID));
		registry.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN));
		registry.setDocument(JsonUtils.getString(json,IJsonNames.DOCUMENT));
		registry.setDocumentCountry(Country.value(JsonUtils.getString(json,IJsonNames.DOCUMENT_COUNTRY)).orElse(null));
		registry.setDocumentType(DocumentType.value(JsonUtils.getString(json,IJsonNames.DOCUMENT_TYPE)).orElse(null));
		registry.setName(JsonUtils.getString(json,IJsonNames.NAME));
		registry.setAlias(JsonUtils.getString(json,IJsonNames.ALIAS));
		registry.setLegalPerson(JsonUtils.getboolean(json,IJsonNames.LEGAL_PERSON));
		registry.setNationality(Country.value(JsonUtils.getString(json,IJsonNames.NATIONALITY)).orElse(null));
		registry.setConfidential(JsonUtils.getboolean(json,IJsonNames.CONFIDENTIAL));
		return registry;
	}
	
	public static JSONObject toJSON(Registry registry) { 
		if (registry == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, registry.getId())
			.put(IJsonNames.DOMAIN, registry.getDomain())
			.put(IJsonNames.DOCUMENT, registry.getDocument())
			.put(IJsonNames.DOCUMENT_COUNTRY, Country.value(registry.getDocumentCountry()))
			.put(IJsonNames.DOCUMENT_TYPE, DocumentType.name( registry.getDocumentType()))
			.put(IJsonNames.NAME, registry.getName())
			.put(IJsonNames.ALIAS, registry.getAlias())
			.put(IJsonNames.LEGAL_PERSON, registry.isLegalPerson())
			.put(IJsonNames.NATIONALITY, Country.value(registry.getNationality()))
			.put(IJsonNames.CONFIDENTIAL, registry.isConfidential());
	}
}
