package com.esferalia.aon.occam.api.model.aonsolutions;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.secrets.SECRETS;

public class AonSecret {
	
	public static String getAonSecret() {
		return get(AonSecrets.AON_SECRET);
	}
	
	public static String get(AonSecrets secret) {
		if(secret == null) return null;
		return get(secret.getName(), secret.getDescription());
	}
	
	public static String get(String name, String description) {
		if(AonStringUtils.isBlank(name) || AonStringUtils.isBlank(description)) return null;
		String value = SECRETS.getValue(description);
		JSONObject json = new JSONObject(value);
		return JsonUtils.getString(json, name);
	}

}
