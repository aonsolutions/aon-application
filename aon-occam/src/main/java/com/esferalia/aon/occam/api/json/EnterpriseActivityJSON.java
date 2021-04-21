package com.esferalia.aon.occam.api.json;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonEnterpriseActivityFromJSON;
import com.esferalia.aon.occam.api.json.JsonFunctionalInterfaces.IAonEnterpriseActivityToJSON;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;

public enum EnterpriseActivityJSON {
	ID(
		(activity, json) -> activity.setId(JsonUtils.getInteger(json, IJsonNames.ID)),
		(activity, json) -> json.put(IJsonNames.ID, activity.getId())
	),
	DESCRIPTION(
		(activity, json) -> activity.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION)),
		(activity, json) -> json.put(IJsonNames.DESCRIPTION, activity.getDescription())
	),
	PRINCIPAL(
		(activity, json) -> activity.setPrincipal(JsonUtils.getboolean(json, IJsonNames.PRINCIPAL)),
		(activity, json) -> json.put(IJsonNames.PRINCIPAL, activity.isPrincipal())
	),
	IAE(
		(activity, json) -> activity.setIae(JsonUtils.getInteger(json, IJsonNames.IAE)),
		(activity, json) -> json.put(IJsonNames.IAE, activity.getIae())
	),
	EPIGRAPH(
		(activity, json) -> activity.setEpigraph(JsonUtils.getString(json, IJsonNames.EPIGRAPH)),
		(activity, json) -> json.put(IJsonNames.EPIGRAPH, activity.getEpigraph())
	),
	CNAE(
		(activity, json) -> activity.setCnae(JsonUtils.getInteger(json, IJsonNames.CNAE)),
		(activity, json) -> json.put(IJsonNames.CNAE, activity.getCnae())	
	),
	CNAE_CODE(
		(activity, json) -> activity.setCnaeCode(JsonUtils.getString(json, IJsonNames.CNAE_CODE)),
		(activity, json) -> json.put(IJsonNames.CNAE_CODE, activity.getCnaeCode())
	),
	CNAE_DESCRIPTION(
		(activity, json) -> activity.setCnaeDescription(JsonUtils.getString(json, IJsonNames.CNAE_DESCRIPTION)),
		(activity, json) -> json.put(IJsonNames.CNAE_DESCRIPTION, activity.getCnaeDescription())
	)
	;
	
	private IAonEnterpriseActivityFromJSON fromJSON;
	private IAonEnterpriseActivityToJSON toJSON;
	
	private EnterpriseActivityJSON (IAonEnterpriseActivityFromJSON fromJSON, IAonEnterpriseActivityToJSON toJSON) {
		this.fromJSON = fromJSON;
		this.toJSON = toJSON;
	}
	
	public static JSONObject toJSON(EnterpriseActivity activity) {
		JSONObject json = new JSONObject();
		if (activity != null) {
			for (EnterpriseActivityJSON p : EnterpriseActivityJSON.values()) {
				p.toJSON.to(activity, json);
			}
			return json;
		}
		return null;
	}
	
	public static EnterpriseActivity fromString (String text) {
		JSONObject json = new JSONObject(text);
		return fromJSON(json);
	}
	
	public static EnterpriseActivity fromJSON (JSONObject json) {
		EnterpriseActivity activity = new EnterpriseActivity();
		if (json != null) {
			for (EnterpriseActivityJSON p : EnterpriseActivityJSON.values()) {
				p.fromJSON.from(activity, json);
			}
			return activity;
		}
		return null;
	}
}
