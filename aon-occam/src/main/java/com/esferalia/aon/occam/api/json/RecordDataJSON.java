package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RecordData;

public class RecordDataJSON {
	
	private RecordDataJSON() {
	
	}
	
	public static List<RecordData> fromJSON(JSONArray json) {
		LinkedList<RecordData> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static RecordData fromJSON(JSONObject json) {
		return new RecordData()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
				.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY))
				.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
				.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE))
				.setNotary(JsonUtils.getString(json, IJsonNames.NOTARY))
				.setNumber(JsonUtils.getString(json, IJsonNames.NUMBER))
				.setRecordDate(JsonUtils.getDate(json, IJsonNames.RECORD_DATE))
				.setVolume(JsonUtils.getString(json, IJsonNames.VOLUME))
				.setSection(JsonUtils.getString(json, IJsonNames.SECTION))
				.setPage(JsonUtils.getString(json, IJsonNames.PAGE))
				.setSheet(JsonUtils.getString(json, IJsonNames.SHEET))
				.setRegistration(JsonUtils.getString(json, IJsonNames.REGISTRATION))
				.setAttach(JsonUtils.getInteger(json, IJsonNames.ATTACH))
				;
	}
	
	public static JSONArray toJSON(List<RecordData> recordDatas) {
		return toJSON(recordDatas.stream());
	}
	
	public static JSONArray toJSON(Stream<RecordData> recordDatas) {
		JSONArray array = new JSONArray();
		recordDatas.forEach(brand -> array.put(toJSON(brand)));
		return array;
	}
	
	
	public static JSONObject toJSON(RecordData recordData) {
		return new JSONObject()
				.put(IJsonNames.ID, recordData.getId())
				.put(IJsonNames.DOMAIN, recordData.getDomain())
				.put(IJsonNames.REGISTRY, recordData.getRegistry())
				.put(IJsonNames.DESCRIPTION, recordData.getDescription())
				.put(IJsonNames.CREATION_DATE, recordData.getCreationDate())
				.put(IJsonNames.NOTARY, recordData.getNotary())
				.put(IJsonNames.NUMBER, recordData.getNumber())
				.put(IJsonNames.RECORD_DATE, recordData.getRecordDate())
				.put(IJsonNames.VOLUME, recordData.getVolume())
				.put(IJsonNames.SECTION, recordData.getSection())
				.put(IJsonNames.PAGE, recordData.getPage())
				.put(IJsonNames.SHEET, recordData.getSheet())
				.put(IJsonNames.REGISTRATION, recordData.getRegistration())
				.put(IJsonNames.ATTACH, recordData.getAttach());
	}
}
