package com.esferalia.aon.gwt.stat.shared;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JsonParams extends JSONObject {
	private static final DateTimeFormat FORMATTER = DateTimeFormat.getFormat("dd/MM/yyyy");
	private static final JSONNull JSON_NULL = JSONNull.getInstance();
	
	public static String convert(StatParams params) {
		JSONObject json = new JSONObject();

		json.put(IRequestParamsNames.DOMAIN   		,new JSONNumber( params.getDomain()));
		json.put(IRequestParamsNames.FROM_DATE 		,params.getFrom() 			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getFrom())));
		json.put(IRequestParamsNames.TO_DATE  	 	,params.getTo()   			== null? JSON_NULL : new JSONString( FORMATTER.format(params.getTo())));
		json.put(IRequestParamsNames.VIEW_AMOUNTS	,new JSONNumber( params.isViewAmounts()?1:0));
		json.put(IRequestParamsNames.STAT_TYPE 		,params.getStatType()		== null? JSON_NULL : new JSONNumber( params.getStatType().ordinal()));
		json.put(IRequestParamsNames.CHART_TYPE		,params.getChartType()		== null? JSON_NULL : new JSONNumber( params.getChartType()));
		json.put(IRequestParamsNames.REGISTRY 		,params.getRegistry() 		== null? JSON_NULL : new JSONNumber( params.getRegistry()));
		json.put(IRequestParamsNames.PRODUCT 		,params.getProduct() 		== null? JSON_NULL : new JSONNumber( params.getProduct()));
		json.put(IRequestParamsNames.FILTER_ITEMS	,params.getFilterItems()	== null? JSON_NULL : getJSONFilterItems(params.getFilterItems()));
		
		return json.toString();
	}
	
	private static JSONValue getJSONFilterItems(LinkedList<StatFilterItem> filterItems) {
		JSONArray array = null;
		for (StatFilterItem item : filterItems) {
			if (item.isSelected()) {
				if (array == null) array = new JSONArray();
				JSONObject json = new JSONObject();
				json.put(IRequestParamsNames.STAT_FILTER_TYPE ,item.getType()		== null? JSON_NULL : new JSONNumber( item.getType().ordinal()));
				json.put(IRequestParamsNames.ID				  ,item.getId() 		== null? JSON_NULL : new JSONString( item.getId()));	
				json.put(IRequestParamsNames.LABEL			  ,item.getLabel() 		== null? JSON_NULL : new JSONString( item.getLabel()));	
				array.set(array.size(), json);
			}
		}
		return (array == null ? JSON_NULL : array);
	}

}
