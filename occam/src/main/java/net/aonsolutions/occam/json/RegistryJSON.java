package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Registry;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class RegistryJSON {
	
	private RegistryJSON() {
	}
	
	public static List<Registry> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(RegistryJSON::from)
			.toList();		
	}
	
	public static Registry from(JSONObject json) {
		if (json == null) return null; 
		return new Registry()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setDocument(AonJSONUtils.getString(json, AonNames.DOCUMENT))
			.setDocumentType( DocumentType.safeValueOf(AonJSONUtils.getString(json, AonNames.DOCUMENT_TYPE)).orElse(null) )
			.setDocumentCountry( Country.safeValueOf(AonJSONUtils.getString(json, AonNames.DOCUMENT_COUNTRY)).orElse(null) )
			.setName(AonJSONUtils.getString(json, AonNames.NAME))
			.setAlias(AonJSONUtils.getString(json, AonNames.ALIAS))
			.setNationality( Country.safeValueOf(AonJSONUtils.getString(json, AonNames.NATIONALITY)).orElse(null) )
			.setConfidential(AonJSONUtils.getBoolean(json, AonNames.CONFIDENTIAL))
		;
	}
	
	public static JSONArray to(List<Registry> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Registry> stream) {
		return stream
			.map(RegistryJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Registry registry) {
		if (registry == null) return null;
		return new JSONObject()
			.put(AonNames.ID, registry.getId())
			.put(AonNames.DOMAIN, registry.getDomain())
			.putOpt(AonNames.DOCUMENT_TYPE, AonObjectUtils.ifNotNullGet(registry.getDocumentType(), Object::toString ))
			.putOpt(AonNames.DOCUMENT_COUNTRY, AonObjectUtils.ifNotNullGet(registry.getDocumentCountry(), Object::toString ))
			.putOpt(AonNames.DOCUMENT, registry.getDocument())
			.putOpt(AonNames.NAME, registry.getName())
			.putOpt(AonNames.ALIAS, registry.getAlias())
			.putOpt(AonNames.NATIONALITY, AonObjectUtils.ifNotNullGet(registry.getNationality(), Object::toString ))
			.putOpt(AonNames.CONFIDENTIAL, registry.isConfidential())
			;
	}
}
