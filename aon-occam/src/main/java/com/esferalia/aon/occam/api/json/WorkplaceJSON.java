package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class WorkplaceJSON {
	
	private WorkplaceJSON() {
	
	}
	
	public static List<Workplace> fromJSON(JSONArray json) {
		LinkedList<Workplace> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Workplace fromJSON(JSONObject json) {
		return new Workplace()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setEnterprise(JsonUtils.getInteger(json, IJsonNames.ENTERPRISE))
			.setCustomer(JsonUtils.getInteger(json, IJsonNames.CUSTOMER))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setAddress(getAddress(json))
			.setScope(JsonUtils.getInteger(json, IJsonNames.SCOPE))
			.setEconomicAgreement(Administration.safeValueOf(JsonUtils.getString(json, IJsonNames.ECONOMIC_AGREEMENT)))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			;
	}
	
	private static RegistryAddress getAddress(JSONObject json) {
		if(JsonUtils.isJSONObject(json, IJsonNames.ADDRESS))
			return RegistryAddressJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ADDRESS));
		Integer id = JsonUtils.getInteger(json, IJsonNames.ADDRESS);
		return id != null ? new RegistryAddress().setId(id) : null;
	}
	
	public static JSONArray toJSON(List<Workplace> list) {
		return toJSON(AonCollectionUtils.stream(list));
	}
	
	public static JSONArray toJSON(Stream<Workplace> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	
	public static JSONObject toJSON(Workplace object) {
		return new JSONObject()
				.put(IJsonNames.ID, object.getId())
				.put(IJsonNames.DOMAIN, object.getDomain())
				.put(IJsonNames.ENTERPRISE, object.getEnterprise())
				.put(IJsonNames.CUSTOMER, object.getCustomer())
				.put(IJsonNames.DESCRIPTION, object.getDescription())
				.put(IJsonNames.ADDRESS, RegistryAddressJSON.toJSON(object.getAddress()))
				.put(IJsonNames.SCOPE, object.getScope())
				.put(IJsonNames.ECONOMIC_AGREEMENT, object.getEconomicAgreement())
				.put(IJsonNames.ACTIVE, object.isActive())
				;
	}
}
