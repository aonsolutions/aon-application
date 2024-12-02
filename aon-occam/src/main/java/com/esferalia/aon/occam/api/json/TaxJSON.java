package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.TaxType;

public class TaxJSON {
	
	private TaxJSON() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static List<Tax> fromJSON(JSONArray json) {
		LinkedList<Tax> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Tax fromJSON(JSONObject json) {
		return new Tax()
			.setId(json.optInt(IJsonNames.ID))
			.setDomain(json.optInt(IJsonNames.DOMAIN))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setType(TaxType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setPercentage(JsonUtils.getdouble(json, IJsonNames.PERCENTAGE))
			.setSurcharge(JsonUtils.getdouble(json, IJsonNames.SURCHARGE))
			;

	}
	
	public static JSONArray toJSON(List<Tax> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Tax> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(tax -> array.put(toJSON(tax)));
		return array;
	}
	
	public static JSONObject toJSON(Tax tax) {
		return new JSONObject()
			.put(IJsonNames.ID, tax.getId())
			.put(IJsonNames.DOMAIN, tax.getDomain())

			// ...
			
			;
	}
}
