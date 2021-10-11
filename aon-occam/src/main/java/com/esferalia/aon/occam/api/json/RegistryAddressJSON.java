package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;

public class RegistryAddressJSON {
	
	private RegistryAddressJSON() {
	
	}
	
	public static List<RegistryAddress> fromJSON(JSONArray json) {
		LinkedList<RegistryAddress> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	public static RegistryAddress fromJSON(JSONObject json) {
		if(json == null) return new RegistryAddress();
		return new RegistryAddress()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(json.optInt(IJsonNames.DOMAIN))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setMain(json.optBoolean(IJsonNames.MAIN))
			.setAddress(json.optString(IJsonNames.ADDRESS))
			.setCity(json.optString(IJsonNames.CITY))
			.setProvince(json.optString(IJsonNames.PROVINCE))
			.setCountry(Country.safeValueOf(json.optString(IJsonNames.COUNTRY)))
			.setZip(json.optString(IJsonNames.POSTAL_CODE))
			.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
			.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED));
	}
	
	public static JSONArray toJSON(List<RegistryAddress> addresses) {
		return toJSON(addresses.stream());
	}
	
	public static JSONArray toJSON(Stream<RegistryAddress> addresses) {
		JSONArray array = new JSONArray();
		addresses.forEach(address -> array.put(toJSON(address)));
		return array;
	}
	
	public static JSONObject toJSON(RegistryAddress address) {
		if(address == null || address.isEmpty()) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, address.getId())
			.put(IJsonNames.DOMAIN, address.getDomain())
			.put(IJsonNames.REGISTRY, address.getRegistry())
			.put(IJsonNames.MAIN, address.isMain())
			.put(IJsonNames.ADDRESS, address.getFullAddress())
			.put(IJsonNames.CITY, address.getCity())
			.put(IJsonNames.PROVINCE, address.getChild() != null ? address.getChild().getName(): null)
			.put(IJsonNames.COUNTRY, address.getParent() != null ? address.getParent().getCode() : null)
			.put(IJsonNames.POSTAL_CODE, address.getZip())
			.put(IJsonNames.ZIP, address.getZip())
			.put(IJsonNames.DIRTY, address.isDirty())
			.put(IJsonNames.REMOVED, address.isRemoved());
	}
	
}
