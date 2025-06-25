package com.esferalia.aon.gwt.mod200.shared;

import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsonParams extends JSONObject {
	
	public static String convert(AEATParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN_NAME	,new JSONString( params.getDomainName()));
		json.put(IRequestParamsNames.DOMAIN_ID 		,new JSONNumber( params.getDomainId()));
		json.put(IRequestParamsNames.USER   		,new JSONString( params.getUser()));
		json.put(IRequestParamsNames.MOD   		 	,params.getMod() == null? JSON_NULL : new JSONNumber( params.getMod()));
		json.put(IRequestParamsNames.CERTIFICATE_ID	,params.getCertificateId() == null? JSON_NULL : new JSONNumber( params.getCertificateId()));	
		json.put(IRequestParamsNames.PASS 	 	 	,AonStringUtils.isBlank(params.getPass())? JSON_NULL : new JSONString( params.getPass()));
		json.put(IRequestParamsNames.NAME 	 	 	,AonStringUtils.isBlank(params.getName())? JSON_NULL : new JSONString( params.getName()));
		json.put(IRequestParamsNames.DOCUMENT 	 	,AonStringUtils.isBlank(params.getDocument())? JSON_NULL : new JSONString( params.getDocument()));
		json.put(IRequestParamsNames.NRC 	 		,AonStringUtils.isBlank(params.getNrc())? JSON_NULL : new JSONString( params.getNrc()));
		json.put(IRequestParamsNames.TEST			,new JSONNumber( params.isTest()?1:0));
		return json.toString();
	}
	
}
