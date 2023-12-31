package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Iae;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.type.VATRegime;

public class EnterpriseActivityJSON {
	
	private EnterpriseActivityJSON() {
	
	}
	
	public static List<EnterpriseActivity> fromJSON(JSONArray json) {
		if(json == null) return new LinkedList<>();
		LinkedList<EnterpriseActivity> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static EnterpriseActivity fromJSON(JSONObject json) {
		if(json == null) return new EnterpriseActivity();
		return new EnterpriseActivity()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDescription(JsonUtils.getString(json, IJsonNames.DESCRIPTION))
			.setPrincipal(JsonUtils.getboolean(json, IJsonNames.PRINCIPAL))
			.setIae(new Iae().setId(JsonUtils.getInteger(json, IJsonNames.IAE)))
			.setEpigraph(JsonUtils.getString(json, IJsonNames.EPIGRAPH))
			.setCnae(JsonUtils.getInteger(json, IJsonNames.CNAE))
			.setCnaeCode(JsonUtils.getString(json, IJsonNames.CNAE_CODE))
			.setCnaeDescription(JsonUtils.getString(json, IJsonNames.CNAE_DESCRIPTION))
			.setVatRegime(VATRegime.safeValueOf(JsonUtils.getString(json, IJsonNames.VAT_REGIME)))
			.setVatExemptionCause(VATExemptionCause.safeValueOf(JsonUtils.getString(json, IJsonNames.VAT_EXEMPTION_CAUSE)))
			;
	}
	
	public static JSONArray toJSON(List<EnterpriseActivity> list) {
		if(list == null) return new JSONArray();
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<EnterpriseActivity> stream) {
		if(stream == null) return new JSONArray();
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(EnterpriseActivity object) {
		if(object == null) return new JSONObject();
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DESCRIPTION, object.getDescription())
			.put(IJsonNames.PRINCIPAL,  object.isPrincipal())
			.put(IJsonNames.IAE, object.getIae().getId())
			.put(IJsonNames.EPIGRAPH, object.getEpigraph())
			.put(IJsonNames.CNAE, object.getCnae())
			.put(IJsonNames.CNAE_CODE, object.getCnaeCode())
			.put(IJsonNames.CNAE_DESCRIPTION, object.getCnaeDescription())
			.put(IJsonNames.VAT_REGIME, object.getVatRegime().name())
			.put(IJsonNames.VAT_EXEMPTION_CAUSE, object.getVatExemptionCause() != null ? object.getVatExemptionCause().name() : null)
			;
	}
}
