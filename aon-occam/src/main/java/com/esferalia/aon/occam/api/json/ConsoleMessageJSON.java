package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.console.ConsoleMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType;

public class ConsoleMessageJSON {
	
	private ConsoleMessageJSON() {
		
	}
	
	public static List<ConsoleMessage> fromJSONArray(String jsonArray) {
		JSONArray array = new JSONArray( jsonArray );
		return fromJSON( array );
	}
	
	public static List<ConsoleMessage> fromJSON(JSONArray json) {
		LinkedList<ConsoleMessage> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static ConsoleMessage fromJSON(JSONObject json) {
		if(json == null) return new ConsoleMessage();
		return new ConsoleMessage()
			.setProcessId(JsonUtils.getString(json,IJsonNames.PROCESS_ID))
			.setType( ConsoleMessageType.safeValueOf( JsonUtils.getString(json,IJsonNames.TYPE) ))
			.setMessage(JsonUtils.getString(json, IJsonNames.MESSAGE))
			.setCount(JsonUtils.getInt(json, IJsonNames.COUNT))
			.setProgress(JsonUtils.getInt(json, IJsonNames.PROGRESS))
			.setPercent(JsonUtils.getdouble(json, IJsonNames.PERCENT))
			.setConsoleDomainMessage( ConsoleDomainMessageJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CONSOLE_DOMAIN_MESSAGE)) )
		;
	}

	public static JSONArray toJSON(List<ConsoleMessage> messages) {
		return toJSON(messages.stream());
	}
	
	public static JSONArray toJSON(Stream<ConsoleMessage> messages) {
		JSONArray array = new JSONArray();
		messages.forEach(msg -> array.put(toJSON(msg)));
		return array;
	}
	
	public static JSONObject toJSON(ConsoleMessage message) {
		if(message == null) return new JSONObject();
		return new JSONObject()
			.putOpt(IJsonNames.PROCESS_ID, message.getProcessId())
			.putOpt(IJsonNames.TYPE, message.getType() == null? ConsoleMessageType.PROGRESS.toString() : message.getType().toString())
			.putOpt(IJsonNames.MESSAGE, message.getMessage())
			.put(IJsonNames.COUNT, message.getCount())
			.put(IJsonNames.PROGRESS, message.getProgress())
			.put(IJsonNames.PERCENT, message.getPercent())
			.putOpt(IJsonNames.CONSOLE_DOMAIN_MESSAGE, ConsoleDomainMessageJSON.toJSON(message.getConsoleDomainMessage()) )
			;		
	}

		
}
