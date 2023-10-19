package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.stat.StatData;

public class StatDataJSON {
	
	public static JSONObject toJSON(StatData<String, String, Double> statdata) {
		JSONObject json = new JSONObject();
		
		statdata.getMap().keySet().stream().forEach(mes -> {
			JSONObject typeJSON = new JSONObject();
			statdata.getMap().get(mes).keySet().stream().forEach(type -> {
				typeJSON.put(type, statdata.getMap().get(mes).get(type));
			});
			
			json.put(mes, typeJSON);
		});
		return json;
	}
}
