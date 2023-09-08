package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class CertificateJSON {
	
	private CertificateJSON() {
	
	}
	
	public static List<Certificate> fromJSON(JSONArray json) {
		List<Certificate> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Certificate fromJSON(JSONObject json) {
		return new Certificate()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setDescription(JsonUtils.getString(json, IJsonNames.NAME))
			.setPassword(JsonUtils.getString(json, IJsonNames.PASSWORD));
	}
	
	public static JSONArray toJSON(List<Certificate> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Certificate> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Certificate object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.NAME, object.getDescription())
			.put(IJsonNames.PASSWORD, object.hasPassword());

	}

}
