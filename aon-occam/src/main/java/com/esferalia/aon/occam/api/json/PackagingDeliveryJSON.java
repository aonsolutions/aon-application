package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDelivery;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDeliveryContainer;
import com.esferalia.aon.occam.api.model.warehouse.PackagingDeliveryContent;

public class PackagingDeliveryJSON {

	private PackagingDeliveryJSON() {
	
	}

	public static List<PackagingDelivery> fromJSON(JSONArray array) {
		LinkedList<PackagingDelivery> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(fromJSON(array.getJSONObject(i)));
		}
 		return list;
	}
	
	public static PackagingDelivery fromJSON(JSONObject json) {
		return new PackagingDelivery()
				.setDelivery(JsonUtils.getInteger(json, IJsonNames.DELIVERY))
				.setContainer(containerFromJSON(JsonUtils.getJSONObject(json, IJsonNames.CONTAINER)))
				.setContent(contentFromJSON(JsonUtils.getJSONArray(json, IJsonNames.CONTENT)))
				.setSalesDetail(JsonUtils.getInteger(json, IJsonNames.SALES_DETAIL));
	}
	
	private static PackagingDeliveryContainer containerFromJSON(JSONObject json) {	
		if(json == null) return new PackagingDeliveryContainer();
		return new PackagingDeliveryContainer()
				.setProduct(JsonUtils.getInteger(json, IJsonNames.PRODUCT))
				.setItem(JsonUtils.getInteger(json, IJsonNames.ITEM));

	}
	
	private static List<PackagingDeliveryContent> contentFromJSON(JSONArray array) {
		
		LinkedList<PackagingDeliveryContent> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			JSONObject json = array.getJSONObject(i);
			PackagingDeliveryContent content = new PackagingDeliveryContent()
			.setSource(JsonUtils.getInteger(json, IJsonNames.SOURCE))
			.setComposition(ItemCompositionJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.COMPOSITION)));
			
			list.add(content);
		}
 		return list;

	}
	public static JSONObject toJSON(PackagingDelivery object) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.DELIVERY, object.getDelivery());
		json.put(IJsonNames.SALES_DETAIL, object.getSalesDetail());
		json.put(IJsonNames.CONTAINER, containerToJSON(object.getContainer()));
		json.put(IJsonNames.CONTENT, contentToJSON(object.getContent()));
		return json;
	}
	
	private static JSONObject containerToJSON(PackagingDeliveryContainer container) {
		return new JSONObject()
				.put(IJsonNames.PRODUCT, container.getProduct())
				.put(IJsonNames.ITEM, container.getItem());
	}
	
	private static JSONArray contentToJSON(List<PackagingDeliveryContent> content) {
		JSONArray arr = new JSONArray();
		content.stream().forEach(c -> {
			arr.put(new JSONObject()
				.put(IJsonNames.SOURCE, c.getSource())
				.put(IJsonNames.COMPOSITION, ItemCompositionJSON.toJSON(c.getComposition())));
		});
		return arr;
	}
}
