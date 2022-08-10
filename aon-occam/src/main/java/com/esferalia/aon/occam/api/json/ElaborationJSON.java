package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;

public class ElaborationJSON {
	
	private ElaborationJSON() {
	
	}
	
	public static List<Elaboration> fromJSON(JSONArray json) {
		LinkedList<Elaboration> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Elaboration fromJSON(JSONObject json) {
		return new Elaboration()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
			.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
			.setItem(ItemJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ITEM)))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setWarehouse(WarehouseJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WAREHOUSE)))
			.setQuantity(JsonUtils.getdouble(json, IJsonNames.QUANTITY))
			.setStatus(ElaborationStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setRemarks(JsonUtils.getString(json, IJsonNames.REMARKS))
			.setSource(ElaborationSource.safeValueOf(JsonUtils.getString(json, IJsonNames.SOURCE)))
			.setSourceId(JsonUtils.getInteger(json, IJsonNames.SOURCE_ID))
			.setDetail(ElaborationDetailJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DETAIL)))
			.setPackaging(ElaborationDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.PACKAGING)));
		
	}
	
	public static JSONArray toJSON(List<Elaboration> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Elaboration> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Elaboration object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.SERIES, object.getSeries())
			.put(IJsonNames.NUMBER, object.getNumber())
			.put(IJsonNames.REFERENCE, object.getReferenceCode())
			.put(IJsonNames.ITEM, ItemJSON.toJSON(object.getItem()))
			.put(IJsonNames.DESCRIPTION, object.getDescription())
			.put(IJsonNames.WAREHOUSE, WarehouseJSON.toJSON(object.getWarehouse()))
			.put(IJsonNames.QUANTITY, object.getQuantity())
			.put(IJsonNames.STATUS, object.getStatusName())
			.put(IJsonNames.COMMENTS, object.getComments())
			.put(IJsonNames.REMARKS, object.getRemarks())
			.put(IJsonNames.SOURCE, object.getSourceName())
			.put(IJsonNames.SOURCE_ID, object.getSourceId())
			.put(IJsonNames.DETAIL, ElaborationDetailJSON.toJSON(object.getDetail()))
			.put(IJsonNames.PACKAGING, ElaborationDetailJSON.toJSON(object.getPackaging()))
			.put(IJsonNames.CREATION_USER, object.getCreationUser())
			.put(IJsonNames.CREATION_DATE, object.getCreationDate())
			.put(IJsonNames.MODIFICATION_USER, object.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE, object.getModificationDate());
	}
}
