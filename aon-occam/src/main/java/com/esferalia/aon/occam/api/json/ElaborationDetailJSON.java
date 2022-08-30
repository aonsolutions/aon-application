package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class ElaborationDetailJSON {
	
	private ElaborationDetailJSON() {
	
	}
	
	public static List<ElaborationDetail> fromJSON(JSONArray json) {
		LinkedList<ElaborationDetail> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ElaborationDetail fromJSON(JSONObject json) {
		return new ElaborationDetail()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setWarehouse(WarehouseJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WAREHOUSE)))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setAddInfo(JsonUtils.getString(json, "addInfo"))
			.setType(ElaborationDetailType.safeValueOf(JsonUtils.getString(json, "type")))
			.setComposition(ElaborationDetailCompositionJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.COMPOSITION)));

	}
	
	public static JSONArray toJSON(List<ElaborationDetail> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<ElaborationDetail> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(ElaborationDetail object) {
		if(object == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.DATE, object.getDate())
			.put(IJsonNames.ITEM, ItemJSON.toJSON(object.getItem()))
			.put(IJsonNames.WAREHOUSE, WarehouseJSON.toJSON(object.getWarehouse()))
			.put(IJsonNames.QUANTITY, object.getQuantity())
			.put(IJsonNames.COMPOSITION, ElaborationDetailCompositionJSON.toJSON(object.getComposition()))
			.put(IJsonNames.CREATION_USER, object.getCreationUser())
			.put(IJsonNames.CREATION_DATE, object.getCreationDate())
			.put(IJsonNames.MODIFICATION_USER, object.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, object.getModificationDate());
	}
}
