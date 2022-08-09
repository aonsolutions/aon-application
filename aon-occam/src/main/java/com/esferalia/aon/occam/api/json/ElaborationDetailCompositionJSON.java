package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class ElaborationDetailCompositionJSON {
	
	private ElaborationDetailCompositionJSON() {
	
	}
	
	public static List<ElaborationDetailComposition> fromJSON(JSONArray json) {
		LinkedList<ElaborationDetailComposition> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ElaborationDetailComposition fromJSON(JSONObject json) {
		return new ElaborationDetailComposition()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setWarehouse(WarehouseJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WAREHOUSE)))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setAddInfo(JsonUtils.getString(json, "addInfo"));
	}
	
	public static JSONArray toJSON(List<ElaborationDetailComposition> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<ElaborationDetailComposition> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(ElaborationDetailComposition object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
		
			.put(IJsonNames.ITEM, ItemJSON.toJSON(object.getItem()))
			.put(IJsonNames.WAREHOUSE, WarehouseJSON.toJSON(object.getWarehouse()))
			.put(IJsonNames.QUANTITY, object.getQuantity())
			.put("addInfo", object.getAddInfo())
			.put(IJsonNames.CREATION_USER, object.getCreationUser())
			.put(IJsonNames.CREATION_DATE, object.getCreationDate())
			.put(IJsonNames.MODIFICATION_USER, object.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, object.getModificationDate());
	}
}
