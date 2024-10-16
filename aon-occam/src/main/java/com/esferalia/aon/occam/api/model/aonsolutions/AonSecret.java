package com.esferalia.aon.occam.api.model.aonsolutions;

import java.util.Base64;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Certificate;
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
		if(AonStringUtils.isBlank(name)) return null;
		JSONObject json = getJSON(description);
		return JsonUtils.getString(json, name);
	}
	
	private static JSONObject getJSON(String description) {
		if(AonStringUtils.isBlank(description)) return null;
		String value = SECRETS.getValue(description);
		return new JSONObject(value);
	}
	
	
	public static Certificate getAonCert() {
		JSONObject json = getJSON(AonSecrets.AON_CERT.getDescription());
		String cert = JsonUtils.getString(json, AonSecrets.AON_CERT.getName());
		String password = JsonUtils.getString(json, AonSecrets.AON_PASSWORD.getName());
		
		byte[] data = Base64.getDecoder().decode(cert);

		return new Certificate()
			.setData(data)
			.setPassword(password);
	}

}
