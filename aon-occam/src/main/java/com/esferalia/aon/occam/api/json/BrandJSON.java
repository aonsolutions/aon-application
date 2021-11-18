package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.product.Brand;

public class BrandJSON {
	
	private BrandJSON() {
	
	}
	
	public static List<Brand> fromJSON(JSONArray json) {
		LinkedList<Brand> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Brand fromJSON(JSONObject json) {
		return new Brand()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setName(JsonUtils.getString(json, IJsonNames.NAME));
	}
	
	public static JSONArray toJSON(List<Brand> brands) {
		return toJSON(brands.stream());
	}
	
	public static JSONArray toJSON(Stream<Brand> brands) {
		JSONArray array = new JSONArray();
		brands.forEach(brand -> array.put(toJSON(brand)));
		return array;
	}
	
	
	public static JSONObject toJSON(Brand brand) {
		return new JSONObject()
				.put(IJsonNames.ID, brand.getId())
				.put(IJsonNames.DOMAIN, brand.getDomain())
				.put(IJsonNames.NAME, brand.getName());
	}
}
