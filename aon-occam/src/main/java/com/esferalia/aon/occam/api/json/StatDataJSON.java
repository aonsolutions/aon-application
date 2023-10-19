package com.esferalia.aon.occam.api.json;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.stat.StatData;

public class StatDataJSON {
	
	public static JSONObject toJSON(StatData<String, String, Double> statdata) {
		JSONObject json = new JSONObject();
		JSONArray list = new JSONArray(); 
		statdata.getMap().keySet().stream().forEach(mes -> {
			JSONObject typeJSON = new JSONObject();
			statdata.getMap().get(mes).keySet().stream().forEach(type -> {
				typeJSON.put(type, statdata.getMap().get(mes).get(type));
			});
			typeJSON.put(IJsonNames.DATE, mes);
			list.put(typeJSON);
		});
		json.put("data", list);
		return json;
	}
}
