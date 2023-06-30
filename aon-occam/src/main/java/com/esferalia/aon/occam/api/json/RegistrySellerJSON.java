package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.type.RegistrySellerStatus;
import com.esferalia.aon.occam.api.model.type.RegistrySellerType;

public class RegistrySellerJSON {

	private RegistrySellerJSON() {
		
	}
	
	public static List<RegistrySeller> fromJSON(JSONArray json) {
		LinkedList<RegistrySeller> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RegistrySeller fromJSON(JSONObject json) {
		if(json == null) return new RegistrySeller();
		return new RegistrySeller()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setSeller(SellerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SELLER)))
				.setStartDate(JsonUtils.getDate(json, IJsonNames.START_DATE))
				.setEndDate(JsonUtils.getDate(json, IJsonNames.END_DATE))
				.setStatus(RegistrySellerStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
				.setType(RegistrySellerType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
				;
	}
	
	public static JSONArray toJSON(List<RegistrySeller> items) {
		return toJSON(items.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistrySeller> items) {
		JSONArray array = new JSONArray();
		items.forEach(item -> array.put(toJSON(item)));
		return array;
	}
	
	
	public static JSONObject toJSON(RegistrySeller seller) {
		if(seller == null) return new JSONObject();
		return new JSONObject()
				.put(IJsonNames.ID, seller.getId())
				.put(IJsonNames.DOMAIN, seller.getDomain())
				.put(IJsonNames.REGISTRY, seller.getRegistry())
				.put(IJsonNames.SELLER, SellerJSON.toJSON(seller.getSeller()))
				.put(IJsonNames.START_DATE, seller.getStartDate())
				.put(IJsonNames.END_DATE, seller.getEndDate())
				.put(IJsonNames.STATUS, seller.getStatus())
				.put(IJsonNames.TYPE, seller.getType())
				;
	}
}
