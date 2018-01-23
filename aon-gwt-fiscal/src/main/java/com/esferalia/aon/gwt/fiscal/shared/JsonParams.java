package com.esferalia.aon.gwt.fiscal.shared;

import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

public class JsonParams extends JSONObject {
	private static final DateTimeFormat FORMATTER = DateTimeFormat.getFormat("dd/MM/yyyy");
	 
	public static String convert(VatParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.REGISTRY 		,params.getRegistry() 			== null? JSON_NULL : new JSONNumber( params.getRegistry()));
		json.put(IRequestParamsNames.ACTIVITY 		,params.getActivity() 			== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFromDate() 			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getToDate()   			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.PERCENT  		,params.getPercent()  			== null? JSON_NULL : new JSONNumber( params.getPercent()));
		json.put(IRequestParamsNames.TYPE   	  	,params.getVatSummaryType() 	== null? JSON_NULL : new JSONNumber( params.getVatSummaryType().ordinal()));
		json.put(IRequestParamsNames.OUTPUT   		,params.getOutput() 			== null? JSON_NULL : new JSONNumber( params.getOutput()?1:0));
		json.put(IRequestParamsNames.SURCHARGE		,params.getSurcharge() 			== null? JSON_NULL : new JSONNumber( params.getSurcharge()?1:0));
		json.put(IRequestParamsNames.FARMER_REGIME	,params.getFarmerRegime() 		== null? JSON_NULL : new JSONNumber( params.getFarmerRegime()?1:0));
		json.put(IRequestParamsNames.ACCRUAL_REGIME	,params.getAccrualRegime() 		== null? JSON_NULL : new JSONNumber( params.getAccrualRegime()?1:0));
		json.put(IRequestParamsNames.INVESTMENT		,params.getInvestment() 		== null? JSON_NULL : new JSONNumber( params.getInvestment()?1:0));
		json.put(IRequestParamsNames.SERVICE		,params.getService() 			== null? JSON_NULL : new JSONNumber( params.getService()?1:0));
		json.put(IRequestParamsNames.RECTIFICATION	,params.getRectificationType()	== null? JSON_NULL : new JSONNumber( params.getRectificationType().ordinal()));
		return json.toString();
	}
	
	public static String convert(IRPFParams params) {
		JSONObject json = new JSONObject();
		JSONNull JSON_NULL = JSONNull.getInstance();
		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.REGISTRY 		,params.getRegistry() 			== null? JSON_NULL : new JSONNumber( params.getRegistry()));
		json.put(IRequestParamsNames.ACTIVITY 		,params.getActivity() 			== null? JSON_NULL : new JSONNumber( params.getActivity()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFromDate() 			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFromDate())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getToDate()   			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getToDate())));
		json.put(IRequestParamsNames.PERCENT  		,params.getPercent()  			== null? JSON_NULL : new JSONNumber( params.getPercent()));
		json.put(IRequestParamsNames.TYPE   	  	,params.getWithholdingType()	== null? JSON_NULL : new JSONNumber( params.getWithholdingType().ordinal()));
		json.put(IRequestParamsNames.OUTPUT   		,params.getOutput() 			== null? JSON_NULL : new JSONNumber( params.getOutput()?1:0));
		json.put(IRequestParamsNames.ACCRUAL_REGIME	,params.getAccrualRegime() 		== null? JSON_NULL : new JSONNumber( params.getAccrualRegime()?1:0));
		json.put(IRequestParamsNames.INVESTMENT		,params.getInvestment() 		== null? JSON_NULL : new JSONNumber( params.getInvestment()?1:0));
		json.put(IRequestParamsNames.SERVICE		,params.getService() 			== null? JSON_NULL : new JSONNumber( params.getService()?1:0));
		json.put(IRequestParamsNames.RECTIFICATION	,params.getRectificationType()	== null? JSON_NULL : new JSONNumber( params.getRectificationType().ordinal()));
		return json.toString();
	}
}
