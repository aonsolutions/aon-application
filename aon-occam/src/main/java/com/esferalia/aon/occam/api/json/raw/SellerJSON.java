package com.esferalia.aon.occam.api.json.raw;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.CommissionTypeJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.json.ScopeJSON;
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
	
	public static Seller fromJSON(JSONObject json) {
		if (json == null) return null;
		return new Seller()
			.copy(RegistryJSON.fromJSON(json))
			.setCommissionType(CommissionTypeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.COMMISSION_TYPE)))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE));
	}
	

	public static JSONArray toJSON(List<Seller> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Seller> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Seller seller) {
		if(seller == null) return null;
		return RegistryJSON.toJSON(seller)
			.put(IJsonNames.COMMISSION_TYPE, CommissionTypeJSON.toJSON(seller.getCommissionType()))
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(seller.getScope()))
			.put(IJsonNames.ACTIVE, seller.isActive());
	}
}
