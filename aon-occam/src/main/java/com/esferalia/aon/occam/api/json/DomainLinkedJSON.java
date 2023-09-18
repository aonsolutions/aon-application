package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.DomainLinked;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class DomainLinkedJSON {
	
	private DomainLinkedJSON() {
		
	}
	
	public static List<DomainLinked> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<DomainLinked> fromJSON(JSONArray json) {
		LinkedList<DomainLinked> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static DomainLinked fromJSON(JSONObject json) {
		if(json == null) return new DomainLinked();
		return new DomainLinked()
			.setRegistry(JsonUtils.getInteger(json,IJsonNames.REGISTRY))
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setSchema(JsonUtils.getString(json, IJsonNames.SCHEMA))
			.setType(JsonUtils.getString(json, IJsonNames.TYPE))
			.setIndex(JsonUtils.getInteger(json, IJsonNames.INDEX))
		;
	}

	public static JSONArray toJSON(List<DomainLinked> domainsLinked) {
		return toJSON(domainsLinked.stream());
	}
	
	public static JSONArray toJSON(Stream<DomainLinked> domainsLinked) {
		JSONArray array = new JSONArray();
		domainsLinked.forEach(task -> array.put(toJSON(task)));
		return array;
	}
	
	public static JSONObject toJSON(DomainLinked domainLinked) {
		if(domainLinked == null) return new JSONObject();
		return new JSONObject()
			.putOpt(IJsonNames.REGISTRY, domainLinked.getRegistry())
			.putOpt(IJsonNames.ID, domainLinked.getId())
			.putOpt(IJsonNames.NAME, domainLinked.getName())
			.putOpt(IJsonNames.SCHEMA, domainLinked.getSchema())
			.putOpt(IJsonNames.TYPE, domainLinked.getType())
			.putOpt(IJsonNames.INDEX, domainLinked.getIndex())
			;		
	}

		
}
