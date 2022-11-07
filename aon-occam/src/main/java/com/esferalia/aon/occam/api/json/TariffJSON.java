package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.Tariff;

public class TariffJSON {
	
	private TariffJSON() {
	
	}
	
	public static List<Tariff> fromJSON(JSONArray json) {
		LinkedList<Tariff> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	
	public static Tariff fromJSON(JSONObject json) {
		if(json==null) return new Tariff();
		return new Tariff()
			.setDomain(DomainJSON.fromJSON(json.optJSONObject(IJsonNames.DOMAIN)).getId())
			.setCode(JsonUtils.getString(json, IJsonNames.CODE))
			.setName(JsonUtils.getString(json, IJsonNames.NAME))
			.setPurchase(JsonUtils.getboolean(json, "purchase"))
			.setDiscount(JsonUtils.getdouble(json, IJsonNames.DISCOUNT))
			.setActive(JsonUtils.getboolean(json, IJsonNames.ACTIVE))
			;
	}
	

	public static JSONArray toJSON(List<Tariff> tariffs) {
		return toJSON(tariffs.stream());
	}
	
	public static JSONArray toJSON(Stream<Tariff> targets) {
		JSONArray array = new JSONArray();
		targets.forEach(target -> array.put(toJSON(target)));
		return array;
	}
	
	public static JSONObject toJSON(Tariff tariff) {
		return new JSONObject()
			.put(IJsonNames.DOMAIN, tariff.getDomain())
			.put(IJsonNames.CODE, tariff.getCode())
			.put(IJsonNames.NAME, tariff.getName())
			.put("purchase", tariff.isPurchase())
			.put(IJsonNames.DISCOUNT, tariff.getDiscount())
			.put(IJsonNames.ACTIVE, tariff.isActive())
			;
	}
}
