package com.esferalia.aon.occam.api.json.raw;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.EnterpriseActivityJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetRegime;
import com.esferalia.aon.occam.api.model.InvestAssetType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InvestAssetJSON {
	
	private InvestAssetJSON() {
	
	}
	
	public static List<InvestAsset> fromJSON(JSONArray json) {
		LinkedList<InvestAsset> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static InvestAsset fromJSON(JSONObject json) {
		if (json == null) return null;
		return new InvestAsset()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setActivity(EnterpriseActivityJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ACTIVITY)))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setType(InvestAssetType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setRegime(InvestAssetRegime.safeValueOf(JsonUtils.getString(json, IJsonNames.REGIME)))
			.setStartDate(JsonUtils.getDate(json, IJsonNames.START_DATE))
			.setEndDate(JsonUtils.getDate(json, IJsonNames.END_DATE))
			.setVatPercent(JsonUtils.getdouble(json, IJsonNames.VAT_PERCENT))
			.setRetentionPercent(JsonUtils.getdouble(json, IJsonNames.RETENTION_PERCENT));
	}

	public static JSONArray toJSON(List<InvestAsset> investAssets) {
		return toJSON(investAssets.stream());
	}
	
	public static JSONArray toJSON(Stream<InvestAsset> investAssets) {
		JSONArray array = new JSONArray();
		investAssets.forEach(investAsset -> array.put(toJSON(investAsset)));
		return array;
	}
	
	public static InvestAsset fromString(String text) {
		if (AonStringUtils.isBlank(text)) return null;
		return fromJSON(new JSONObject(text)); 
	}
	
	public static JSONObject toJSON(InvestAsset investAsset) {
		if (investAsset == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, investAsset.getId())
			.put(IJsonNames.DOMAIN, investAsset.getDomain())
			.put(IJsonNames.DESCRIPTION, investAsset.getDescription())
			.put(IJsonNames.ACTIVITY, EnterpriseActivityJSON.toJSON(investAsset.getActivity()))
			.put(IJsonNames.TYPE, investAsset.getType().name())
			.put(IJsonNames.REGIME, investAsset.getRegime().name())
			.put(IJsonNames.START_DATE, AonDateUtils.simpleFormat(investAsset.getStartDate()))
			.put(IJsonNames.END_DATE, AonDateUtils.simpleFormat(investAsset.getEndDate()))
			.put(IJsonNames.VAT_PERCENT, investAsset.getVatPercent())
			.put(IJsonNames.RETENTION_PERCENT, investAsset.getRetentionPercent())
			;
	}
}
