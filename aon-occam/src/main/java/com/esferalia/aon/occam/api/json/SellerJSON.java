package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Seller;

public class SellerJSON {
	
	private SellerJSON() {
	
	}
	
	public static List<Seller> fromJSON(JSONArray json) {
		LinkedList<Seller> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Optional<Seller> from(JSONObject json) {
		if (JsonUtils.isEmpty(json)) return Optional.empty();
		return Optional.of(new Seller()
				.copy(RegistryJSON.fromJSON(json))
				.setCommissionType(CommissionTypeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.COMMISSION_TYPE)))
				.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
				.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE)));

	}
	
	public static Seller fromJSON(JSONObject json) {
		return from( json).orElse( new Seller());
	}
	

	public static JSONArray toJSON(List<Seller> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Seller> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Seller object) {
		if(object == null || object.isEmpty()) return new JSONObject();
		return RegistryJSON.toJSON(object)
			.put(IJsonNames.COMMISSION_TYPE, CommissionTypeJSON.toJSON(object.getCommissionType()))
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(object.getScope()))
			.put(IJsonNames.ACTIVE, object.isActive());
	}
}
