package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.Packaging;

public class PackagingJSON {

	private PackagingJSON() {
	
	}

	public static Packaging fromJSON(JSONObject json) {
		return new Packaging()
			.setBase(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.BASE)))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setContainer(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CONTAINER)))
			.setContainers(ItemJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.CONTAINERS)))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY));
	}
	public static JSONObject toJSON(Packaging object) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.BASE, ItemJSON.toJSON(object.getBase()));
		json.put(IJsonNames.ITEM, ItemJSON.toJSON(object.getItem()));
		json.put(IJsonNames.CONTAINER, ItemJSON.toJSON(object.getContainer()));
		json.put(IJsonNames.CONTAINERS, ItemJSON.toJSON(object.getContainers()));
		return json;
	}
}
