package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageFixType;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;

public class ConsoleDomainMessageJSON {
	
	private ConsoleDomainMessageJSON() {
		
	}
	
	public static List<ConsoleDomainMessage> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<ConsoleDomainMessage> fromJSON(JSONArray json) {
		LinkedList<ConsoleDomainMessage> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ConsoleDomainMessage fromJSON(JSONObject json) {
		if(json == null) return new ConsoleDomainMessage();
		return new ConsoleDomainMessage()
			.setType( ConsoleDomainMessageType.safeValueOf( JsonUtils.getString(json,IJsonNames.TYPE) ))
			.setFixType( ConsoleDomainMessageFixType.safeValueOf( JsonUtils.getString(json,IJsonNames.FIX_TYPE) ))
			.setSchema(JsonUtils.getString(json, IJsonNames.SCHEMA))
			.setDomainId(JsonUtils.getInteger(json,IJsonNames.DOMAIN_ID))
			.setTable(JsonUtils.getString(json, IJsonNames.TABLE))
			.setPkId(JsonUtils.getInteger(json,IJsonNames.PK_ID))
			.setPkCode(JsonUtils.getString(json,IJsonNames.PK_CODE))
			.setFkTable(JsonUtils.getString(json, IJsonNames.FK_TABLE))
			.setFkColumn(JsonUtils.getString(json, IJsonNames.FK_COLUMN))
			.setFkId(JsonUtils.getInteger(json, IJsonNames.FK_ID))
			.setWrongDomainId(JsonUtils.getInteger(json, IJsonNames.WRONG_DOMAIN_ID))
			.setMessage(JsonUtils.getString(json, IJsonNames.MESSAGE))
			;
	}

	public static JSONArray toJSON(List<ConsoleDomainMessage> messages) {
		return toJSON(messages.stream());
	}
	
	public static JSONArray toJSON(Stream<ConsoleDomainMessage> messages) {
		JSONArray array = new JSONArray();
		messages.forEach(msg -> array.put(toJSON(msg)));
		return array;
	}
	
	public static JSONObject toJSON(ConsoleDomainMessage message) {
		if(message == null) return new JSONObject();
		return new JSONObject()
			.putOpt(IJsonNames.TYPE, message.getType() == null? null : message.getType().toString())
			.putOpt(IJsonNames.FIX_TYPE, message.getFixType() == null? null : message.getFixType().toString())
			.putOpt(IJsonNames.SCHEMA, message.getSchema())	
			.putOpt(IJsonNames.DOMAIN_ID, message.getDomainId())	
			.putOpt(IJsonNames.TABLE, message.getTable())	
			.putOpt(IJsonNames.PK_ID, message.getPkId())	
			.putOpt(IJsonNames.PK_CODE, message.getPkCode())
			.putOpt(IJsonNames.FK_TABLE, message.getFkTable())	
			.putOpt(IJsonNames.FK_COLUMN, message.getFkColumn())
			.putOpt(IJsonNames.FK_ID, message.getFkId())
			.putOpt(IJsonNames.WRONG_DOMAIN_ID, message.getWrongDomainId())
			.putOpt(IJsonNames.MESSAGE, message.getMessage())
			;		
	}

		
}
