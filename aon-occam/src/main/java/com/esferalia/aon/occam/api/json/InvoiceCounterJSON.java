package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.InvoiceCounter;

public class InvoiceCounterJSON {
	
	private InvoiceCounterJSON() {

	}
	
	public static JSONObject toJSON(InvoiceCounter counter) {
		JSONObject json = new JSONObject();
		counter.getMap().keySet().stream().forEach(key -> {
			if(JsonUtils.has(json, key.getTediName())) {
				Integer count = JsonUtils.getInteger(json, key.getTediName());
				json.put(key.getTediName(), counter.getMap().get(key) + count);
			} else json.put(key.getTediName(), counter.getMap().get(key));
		});
		return json;
	}

}
