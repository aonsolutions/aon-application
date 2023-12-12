package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.ItemComposition;

public class ItemCompositionJSON {
	
	private ItemCompositionJSON() {
	
	}
	
	public static List<ItemComposition> fromJSON(JSONArray json) {
		LinkedList<ItemComposition> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ItemComposition fromJSON(JSONObject json) {
		return new ItemComposition()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setItemId(JsonUtils.getInteger(json, IJsonNames.ITEM))
			.setCompositionItemId(JsonUtils.getInteger(json, IJsonNames.COMPOSITION_ITEM))
			
			.setComposition(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.COMPOSITION)))

			.setSequence(JsonUtils.getInteger(json, IJsonNames.SEQUENCE))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setQuantity(JsonUtils.getDouble(json, IJsonNames.QUANTITY))
			.setDiscountExpression(JsonUtils.getString(json, IJsonNames.DISCOUNT));
	}
	
	public static JSONArray toJSON(List<ItemComposition> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<ItemComposition> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(ItemComposition object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.ITEM, object.getItemId())
			.put(IJsonNames.COMPOSITION_ITEM, object.getCompositionItemId())
			.put(IJsonNames.COMPOSITION, ItemJSON.toJSON(object.getComposition()))
			.put(IJsonNames.SEQUENCE, object.getSequence())
			.put(IJsonNames.DESCRIPTION, object.getDescription())
			.put(IJsonNames.QUANTITY, object.getQuantity())
			.put(IJsonNames.DISCOUNT, object.getDiscountExpression());
	}
}
