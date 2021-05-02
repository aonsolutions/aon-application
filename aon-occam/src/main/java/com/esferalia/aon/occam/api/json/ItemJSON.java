package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.product.Item;

public class ItemJSON {

	public static LinkedList<Item> fromJSON(JSONArray json) {
		LinkedList<Item> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Item fromJSON(JSONObject json) {
		return new Item()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID));
//				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN));
//				.setCode(JsonUtils.getString(json, IJsonNames.CODE))
//				.setName(JsonUtils.getString(json, IJsonNames.NAME));
	}
	
	public static JSONArray toJSON(LinkedList<Item> items) {
		return toJSON(items.stream());
	}
	
	public static JSONArray toJSON(Stream<Item> items) {
		JSONArray array = new JSONArray();
		items.forEach(item -> array.put(toJSON(item)));
		return array;
	}
	
	
	public static JSONObject toJSON(Item item) {
		return new JSONObject()
				.put(IJsonNames.ID, item.getId())
				.put(IJsonNames.DOMAIN, item.getDomain().getId())
				.put(IJsonNames.CODE, item.getProduct().getCode())
				.put(IJsonNames.NAME, item.getProduct().getName())
				.put(IJsonNames.PRICE, item.getPrice())
				.put(IJsonNames.DESCRIPTION, item.getDescription());
	}
}
