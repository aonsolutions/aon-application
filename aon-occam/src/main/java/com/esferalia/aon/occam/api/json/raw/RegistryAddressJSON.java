package com.esferalia.aon.occam.api.json.raw;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.StreetType;

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
		if(json == null) return null;
		return new RegistryAddress()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(json.optInt(IJsonNames.DOMAIN))
			.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
			.setMain(json.optBoolean(IJsonNames.MAIN))
			.setStreetType(StreetType.safeValueOf(JsonUtils.getString(json, IJsonNames.STREET_TYPE)))
			.setAddress(json.optString(IJsonNames.ADDRESS))
			.setNumber(json.optString(IJsonNames.NUMBER))
			.setAddress2(json.optString(IJsonNames.ADDRESS2))
			.setCity(json.optString(IJsonNames.CITY))
			.setProvince(json.optString(IJsonNames.PROVINCE))
			.setCountry(Country.safeValueOf(json.optString(IJsonNames.COUNTRY)))
			.setZip(json.optString(IJsonNames.ZIP))
			.setDirty(JsonUtils.getboolean(json, IJsonNames.DIRTY))
			.setRemoved(JsonUtils.getboolean(json, IJsonNames.REMOVED))
			.setGlobal(JsonUtils.getboolean(json, IJsonNames.GLOBAL));
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
		if(address == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, address.getId())
			.put(IJsonNames.DOMAIN, address.getDomain())
			.put(IJsonNames.REGISTRY, address.getRegistry())
			.put(IJsonNames.MAIN, address.isMain())
			.put(IJsonNames.ALIAS, address.getAlias())
			.put(IJsonNames.STREET_TYPE, address.getStreetType() != null ? address.getStreetType().getIneCode() : "")
			.put(IJsonNames.ADDRESS, address.getAddress() != null ? address.getAddress() : "")
			.put(IJsonNames.NUMBER, address.getNumber() != null ? address.getNumber() : "")
			.put(IJsonNames.ADDRESS2, (address.getAddress2() != null ? address.getAddress2() : "") + (address.getAddress2() != null && address.getAddress3() != null  ? " " : "") + (address.getAddress3() != null ? address.getAddress3() : ""))
			.put(IJsonNames.CITY, address.getCity() != null ? address.getCity() : "")
			.put(IJsonNames.PROVINCE, address.getChild() != null ? address.getChild().getName(): "")
			.put(IJsonNames.COUNTRY, address.getParent() != null ? address.getParent().getCode() : "ES")
			.put(IJsonNames.POSTAL_CODE, address.getZip())
			.put(IJsonNames.ZIP, address.getZip())
			.put(IJsonNames.DIRTY, address.isDirty())
			.put(IJsonNames.REMOVED, address.isRemoved())
			.put(IJsonNames.FULL_ADDRESS, address.getFullAddress2());
	}
	
}
